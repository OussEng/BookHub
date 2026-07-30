# Scripts SQL — module Réservation

Ces objets ne sont pas créés par Hibernate. `ddl-auto=update` gère les tables,
rien d'autre : si la base est recréée, **tout ce dossier disparaît sans erreur**
et l'application continue de démarrer normalement. Les index de la file
disparaissent simplement, et les requêtes redeviennent des balayages.

À rejouer donc après chaque recréation de la base.

## Ce qu'on applique aujourd'hui

| # | Fichier | Objet | À appliquer |
|---|---------|-------|-------------|
| 1 | `ux_reservation_active.sql` | index unique filtré | **oui** |
| 2 | `ix_reservation_file.sql` | index de la file | **oui** |
| 3 | `data_test.sql` | jeu de démonstration | **oui**, en dernier |
| 4 | `usp_promouvoir_file.sql` | procédure stockée | **non** — voir ci-dessous |
| 5 | `trg_loans_retour.sql` | déclencheur sur `loans` | **non** — voir ci-dessous |

### Pourquoi la procédure et le déclencheur restent hors ligne

La file d'attente est désormais pilotée par Java : `ReservationService.promote()`
est appelée depuis `LoanService.returnLoan`, depuis l'annulation et depuis la
tâche planifiée. Installer le déclencheur ferait avancer la file **deux fois**
pour un même retour : deux lecteurs recevraient le même exemplaire.

Ces deux fichiers sont conservés parce que le cahier des charges demande des
procédures stockées et des déclencheurs, et parce qu'ils documentent la
protection contre la concurrence que la version Java n'a pas encore
(`UPDLOCK` + `READPAST`). Ils ne sont pas à jour : ils portent encore
l'ancien statut `AVAILABLE`, renommé depuis en `READY_FOR_PICKUP`.

Avant toute remise en service : mettre les valeurs à jour, ajouter le
paramètre `@from_status` que la version Java possède déjà, et retirer l'appel
Java — l'un ou l'autre, jamais les deux.

## Application

Après le **premier démarrage de l'application**, jamais avant : les tables
sont créées par Hibernate au démarrage.

Le conteneur ne voit pas le dossier du projet — aucun volume n'est monté.
`-i sql/…` échoue donc avec `Invalid filename`. Il faut passer le contenu
par l'entrée standard, que `docker exec -i` transmet :

```bash
cd <racine du projet>
for f in sql/ux_reservation_active.sql sql/ix_reservation_file.sql sql/data_test.sql; do
  docker exec -i bookhub-sql /opt/mssql-tools18/bin/sqlcmd \
    -S localhost -U sa -P '<mot de passe>' -C -b -d BookHub < "$f"
done
```

`-b` interrompt à la première erreur ; sans lui, un échec passe inaperçu.

## Comptes de connexion

`data_test.sql` insère huit comptes. Les mots de passe sont hachés en BCrypt
dans le script ; en clair, ils valent :

| Comptes | Rôle | Mot de passe |
|---------|------|--------------|
| `user@test` … `user5@test` | `ROLE_USER` | `User@test1` |
| `admin@test` | `ROLE_ADMIN` | `Admin@test1` |
| `librarian@test` | `ROLE_LIBRARIAN` | `Libra@test1` |

Tous respectent la règle de `RegisterRequest` : au moins 8 caractères, une
majuscule, une minuscule, un chiffre et un caractère parmi `@$!%*?&`.
Données fictives, réservées à la démonstration — à ne pas reprendre tel quel
dans un projet qui manipule de vrais utilisateurs.

## Vérifier ce qui est en place

```sql
SELECT name, type_desc FROM sys.objects WHERE type IN ('P', 'TR');
SELECT name, filter_definition FROM sys.indexes WHERE object_id = OBJECT_ID('reservation');
SELECT reservation_status, COUNT(*) FROM reservation GROUP BY reservation_status;
```

Attendu : aucune procédure ni déclencheur, trois index sur `reservation`
(clé primaire, `ux_reservation_active`, `ix_reservation_file`), et vingt
réservations réparties sur les cinq statuts.

## Points à connaître

**`SET QUOTED_IDENTIFIER ON` vaut pour toutes les écritures, pas seulement
pour la création de l'index.** Un index filtré impose ce réglage à chaque
`INSERT`, `UPDATE` et `DELETE` sur la table concernée. `sqlcmd` le laisse à
`OFF` par défaut : sans la ligne en tête de script, même un `DELETE FROM
reservation` échoue avec l'erreur 1934. Les pilotes JDBC et SSMS le mettent
à `ON` d'eux-mêmes, l'application n'est donc jamais concernée.

**`DBCC CHECKIDENT(..., RESEED, 0)` ne remet pas le compteur à 1 partout.**
Sur une table où aucune ligne n'a jamais été insérée, la valeur donnée est
utilisée telle quelle : la première ligne reçoit l'identifiant 0. Sur une
table qui a déjà servi, c'est valeur + 1. `data_test.sql` n'en dépend donc
pas : il impose les identifiants des livres, auteurs et genres par
`SET IDENTITY_INSERT`, puis resynchronise le compteur.

**Le préfixe `sp_` est évité** : SQL Server le réserve aux procédures système
et les cherche dans `master` avant la base courante. D'où `usp_`.

**Heure UTC.** Java écrit `reserves_date`, `notified_at` et `pickup_deadline`
avec `LocalDateTime.now(ZoneOffset.UTC)` ; `data_test.sql` utilise
`SYSUTCDATETIME()`. Les dates de prêt sont des `DATE` et passent par
`GETDATE()`. Exception connue : `Review.createdAt` est renseigné par
`@PrePersist` avec l'heure de la JVM, il est donc décalé.
