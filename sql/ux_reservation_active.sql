-- Empêche deux réservations vivantes du même lecteur sur le même livre.
-- Dernier rempart contre le double clic : deux requêtes simultanées peuvent
-- toutes deux passer le contrôle applicatif, la base refusera la seconde.
-- QUOTED_IDENTIFIER doit être ON : obligatoire pour un index filtré.
--
-- À exécuter APRÈS le premier démarrage de l'application :
-- la table reservation est créée par Hibernate, pas par ce script.
-- ddl-auto=update ne gère pas cet index : si la base est recréée,
-- il disparaît sans erreur et la protection saute silencieusement.

SET QUOTED_IDENTIFIER ON;
GO

CREATE UNIQUE INDEX ux_reservation_active
    ON reservation (users_id, book_id)
    WHERE reservation_status IN ('PENDING', 'AVAILABLE');
GO