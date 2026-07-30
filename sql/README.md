# Scripts SQL — module Réservation

Ces objets ne sont pas créés par Hibernate. `ddl-auto=update` gère les tables,
rien d'autre : si la base est recréée, **tout ce dossier disparaît sans erreur**
et l'application continue de démarrer normalement. La file d'attente cesse
simplement d'avancer.

À rejouer donc après chaque recréation de la base.

## Ordre d'application

| # | Fichier | Objet | Dépend de |
|---|---------|-------|-----------|
| 1 | `ux_reservation_active.sql` | index unique filtré | table `reservation` |
| 2 | `ix_reservation_file.sql` | index de la file | table `reservation` |
| 3 | `usp_promouvoir_file.sql` | procédure stockée | tables `reservation`, `book_copies` |
| 4 | `trg_loans_retour.sql` | déclencheur sur `loans` | table `loans` + procédure (3) |

L'ordre n'est pas décoratif :

- **3 avant 4** — `trg_loans_retour` appelle `usp_promouvoir_file`.
- **4 en dernier** — `CREATE TRIGGER ... ON loans` échoue si la table n'existe pas
  encore. Les index et la procédure passent même sur des tables absentes :
  SQL Server diffère la résolution des noms, pas le déclencheur.

## Quand les appliquer

Après le **premier démarrage de l'application**, jamais avant : les tables
`reservation`, `loans` et `book_copies` sont créées par Hibernate au démarrage.

```bash
docker exec -i <conteneur> /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P '<mot de passe>' -C -d <base> \
  -i sql/ux_reservation_active.sql \
  -i sql/ix_reservation_file.sql \
  -i sql/usp_promouvoir_file.sql \
  -i sql/trg_loans_retour.sql
```

## Vérifier ce qui est en place

```sql
SELECT name, type_desc FROM sys.objects WHERE type IN ('P', 'TR');
SELECT name FROM sys.indexes WHERE object_id = OBJECT_ID('reservation');
```

## Points à connaître

**`SET QUOTED_IDENTIFIER ON`** est obligatoire avant `ux_reservation_active` :
SQL Server le refuse sinon, l'index est filtré.

**Le préfixe `sp_` est évité** : SQL Server le réserve aux procédures système
et les cherche dans `master` avant la base courante. D'où `usp_`.

**Heure UTC des deux côtés.** Java écrit `reserves_date` et `pickup_deadline`
en UTC ; la procédure utilise `SYSUTCDATETIME()` et non `GETDATE()`.

**Le déclencheur filtre sur la valeur actuelle de `LoanStatus`.** Si cette
énumération est renommée côté Java, `trg_loans_retour.sql` doit être modifié
dans le même lot — sinon il cesse de se déclencher, sans aucune erreur.
