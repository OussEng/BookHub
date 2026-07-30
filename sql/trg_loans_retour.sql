-- Fait avancer la file d'attente quand un exemplaire est rendu.
-- Chemin 1 des trois qui appellent usp_promouvoir_file (US-RESA-03).
--
-- DÉCLENCHEMENT : uniquement sur le passage du prêt à RETURN.
-- Pas « le statut vaut RETURN » mais « il ne le valait pas avant » :
-- sinon toute modification ultérieure d'un prêt déjà rendu relancerait
-- la file et promouvrait quelqu'un une seconde fois.
--
-- CURSEUR ASSUMÉ : en SQL Server un déclencheur se déclenche par requête,
-- pas par ligne — inserted peut contenir plusieurs prêts. La procédure
-- prend des paramètres scalaires parce que trois scénarios différents
-- l'appellent. Boucler ici coûte moins que dupliquer la logique de file.
-- En usage réel : une ligne, le bibliothécaire enregistre un retour à la fois.
--
-- PAS DE TRY...CATCH : une erreur doit annuler le retour lui-même.
-- Une file qui cesse d'avancer en silence est pire qu'un refus visible.
--
-- TRANSACTION : celle de l'UPDATE loans, déjà ouverte. La procédure
-- n'en ouvre pas non plus — voir son en-tête.
--
-- Noms vérifiés dans le code de l'équipe : table loans, PK id,
-- colonnes status et book_copy_loaned_id.
--
-- TODO 'RETURN' est la valeur actuelle de LoanStatus.
-- L'équipe avait décidé ACTIVE / RETURNED : quand ce renommage
-- sera appliqué, changer ici dans le même PR. Sinon le déclencheur
-- cesse de se déclencher, sans erreur.
--
-- À exécuter APRÈS le premier démarrage et APRÈS usp_promouvoir_file.

SET QUOTED_IDENTIFIER ON;
GO

CREATE OR ALTER TRIGGER trg_loans_retour
    ON loans
    AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- 0. La colonne de statut n'a pas bougé : rien à faire.
    IF NOT UPDATE(status)
        RETURN;

    DECLARE @book_id      BIGINT;
    DECLARE @book_copy_id BIGINT;

    -- 1. Les prêts qui viennent de passer à RETURN, avec le livre
    --    de l'exemplaire libéré. La jointure sur deleted isole
    --    la transition ; book_copies donne le book_id que la procédure attend.
    DECLARE cur CURSOR LOCAL FAST_FORWARD FOR
        SELECT bc.book_id, i.book_copy_loaned_id
        FROM inserted i
                 JOIN deleted d
                      ON d.id = i.id
                 JOIN book_copies bc
                      ON bc.id = i.book_copy_loaned_id
        WHERE i.status = 'RETURN'
          AND d.status <> 'RETURN';

    OPEN cur;
    FETCH NEXT FROM cur INTO @book_id, @book_copy_id;

    -- 2. Un appel par exemplaire rendu. Le paramètre de sortie
    --    n'est pas récupéré : personne ici n'a besoin de savoir
    --    si quelqu'un a été promu.
    WHILE @@FETCH_STATUS = 0
        BEGIN
            EXEC usp_promouvoir_file @book_id, @book_copy_id;

            FETCH NEXT FROM cur INTO @book_id, @book_copy_id;
        END

    CLOSE cur;
    DEALLOCATE cur;
END
GO
