-- Fait avancer la file d'attente d'un cran pour un livre donné.
--
-- Appelée par trois chemins :
--   1. retour d'un exemplaire                 → déclencheur SQL sur loan (US-RESA-03)
--   2. expiration des 72h                     → tâche @Scheduled
--   3. annulation d'une réservation AVAILABLE → service (US-RESA-02)
-- Écrite une seule fois pour que les trois se comportent exactement pareil.
--
-- TRANSACTION : la procédure n'en ouvre pas. C'est l'appelant qui la tient —
-- le déclencheur est déjà dans celle de l'UPDATE loan, les appels Java
-- doivent être annotés @Transactional. Les transactions imbriquées de
-- SQL Server ne sont pas de vrais points de reprise : un ROLLBACK interne
-- annulerait tout.
--
-- ORDRE DE VERROUILLAGE : reservation d'abord, book_copies ensuite.
-- Tout code qui touche aux deux tables doit garder cet ordre,
-- sinon deux transactions peuvent se bloquer mutuellement.
--
-- Pas de préfixe sp_ : SQL Server le réserve aux procédures système
-- et les cherche dans master avant la base courante.
--
-- Noms vérifiés dans la branche 12 : table book_copies,
-- colonnes id, book_id, book_status ; valeurs AVAILABLE et RESERVED
-- présentes dans BookStatus.
--
-- À exécuter APRÈS le premier démarrage : les tables viennent d'Hibernate.
-- ddl-auto=update ne gère pas les procédures : si la base est recréée,
-- celle-ci disparaît sans erreur et la file cesse d'avancer silencieusement.

SET QUOTED_IDENTIFIER ON;
GO

CREATE OR ALTER PROCEDURE usp_promouvoir_file @book_id BIGINT,
                                              @book_copy_id BIGINT,
                                              @promu BIT = NULL OUTPUT -- 1 si quelqu'un a été promu, 0 sinon
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    SET @promu = 0;

    -- 0. Garde-fou : sans paramètres, tout le reste échouerait en silence.
    IF @book_id IS NULL OR @book_copy_id IS NULL
        RETURN;

    DECLARE @reservation_id BIGINT;
    DECLARE @maintenant DATETIME2(6) = SYSUTCDATETIME();

    -- 1. Le premier de la file, verrouillé le temps de la transaction.
    --    UPDLOCK réserve la ligne pour mise à jour.
    --    READPAST : si un autre exemplaire du même livre est rendu au même
    --    moment, l'autre appel passe au suivant au lieu d'attendre —
    --    sinon les deux promouvraient le même lecteur.
    --    reserves_date fixe l'ordre ; écrit en UTC par Java, d'où
    --    SYSUTCDATETIME() ici et non GETDATE().
    SELECT TOP 1 @reservation_id = reservation_id
    FROM reservation
    WITH (UPDLOCK, ROWLOCK, READPAST)
    WHERE book_id = @book_id
      AND reservation_status = 'PENDING'
    ORDER BY reserves_date ASC;

    -- 2. Personne n'attend : l'exemplaire redevient empruntable par tous.
--    Indispensable pour les appels 2 et 3, où il était RESERVED :
--    sans cette remise à AVAILABLE il resterait bloqué pour toujours.
    IF @reservation_id IS NULL
        BEGIN
            UPDATE book_copies
            SET book_status = 'AVAILABLE'
            WHERE id = @book_copy_id
              AND book_id = @book_id
              AND book_status = 'RESERVED';
            RETURN;
        END

    -- 3. Refus de voler un exemplaire déjà mis de côté pour quelqu'un
    --    d'autre : une réservation AVAILABLE le tient encore.
    IF EXISTS (SELECT 1
               FROM reservation
               WHERE book_copy_id = @book_copy_id
                 AND reservation_status = 'AVAILABLE')
        RETURN;

    -- 4. L'exemplaire est mis de côté. Les conditions écartent un exemplaire
    --    inexistant, d'un autre livre, prêté, perdu ou en réparation.
    --    Zéro ligne touchée : on sort sans rien promouvoir, plutôt que de
    --    laisser une réservation pointer sur du vide.
    UPDATE book_copies
    SET book_status = 'RESERVED'
    WHERE id = @book_copy_id
      AND book_id = @book_id
      AND book_status IN ('AVAILABLE', 'RESERVED');

    IF @@ROWCOUNT = 0
        RETURN;

    -- 5. La réservation passe en tête : 72h fixes pour cliquer « Prendre ».
    --    pickup_deadline est stocké et non recalculé : un changement futur
    --    du délai ne doit pas déplacer les échéances déjà annoncées.
    UPDATE reservation
    SET reservation_status = 'AVAILABLE',
        book_copy_id       = @book_copy_id,
        notified_at        = @maintenant,
        pickup_deadline    = DATEADD(HOUR, 72, @maintenant)
    WHERE reservation_id = @reservation_id;

    SET @promu = 1;
END
GO