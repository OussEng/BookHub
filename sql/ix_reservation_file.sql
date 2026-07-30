-- Sert la requête qui ouvre usp_promouvoir_file, à son étape 1 :
--
--   SELECT TOP 1 reservation_id
--   FROM reservation WITH (UPDLOCK, ROWLOCK, READPAST)
--   WHERE book_id = @book_id AND reservation_status = 'PENDING'
--   ORDER BY reserves_date ASC
--
-- Sans lui, chaque retour d'exemplaire, chaque expiration et chaque
-- annulation balaient toute la table reservation.
--
-- ORDRE DES COLONNES, il n'est pas interchangeable :
--   book_id            égalité, la plus sélective — en tête
--   reservation_status égalité également
--   reserves_date      sert au tri, elle vient donc en dernier :
--                      l'index rend les lignes déjà ordonnées et
--                      SQL Server n'a plus de tri à faire.
--
-- L'index unique ux_reservation_active ne peut pas jouer ce rôle :
-- il porte sur (users_id, book_id), l'ordre des colonnes ne convient pas.
--
-- Pas d'index filtré ici, contrairement à ux_reservation_active :
-- la promotion interroge PENDING, la tâche planifiée interroge
-- READY_FOR_PICKUP. Un filtre sur un seul statut n'en servirait qu'une.
--
-- À exécuter APRÈS le premier démarrage : la table vient d'Hibernate.
-- ddl-auto=update ne recrée pas les index : si la base est refaite,
-- celui-ci disparaît sans erreur et les requêtes redeviennent des balayages.

CREATE INDEX ix_reservation_file
    ON reservation (book_id, reservation_status, reserves_date);
GO
