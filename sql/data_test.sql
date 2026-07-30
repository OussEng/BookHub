-- Jeu de données de démonstration — BookHub
--
-- PRÉREQUIS : base fraîchement recréée.
--   Les contraintes CHECK ne sont pas régénérées par ddl-auto=update.
--   Une base qui date d'avant le renommage AVAILABLE -> READY_FOR_PICKUP
--   refusera les réservations mises de côté. En cas de doute :
--     DROP DATABASE BookHub; CREATE DATABASE BookHub;
--   puis démarrer l'application une fois, puis charger ce script.
--
-- Le script commence par vider les tables : il peut être rejoué.
--
-- Ordre imposé par les clés étrangères :
--   users -> books -> book_copies -> authors -> book_author
--         -> genres -> book_genre -> loans -> reservation -> review
--
-- Mots de passe en clair, hachés en BCrypt dans les lignes ci-dessous.
-- Chaque hachage a été vérifié par checkpw après génération.
-- Le sel est aléatoire : deux comptes au même mot de passe ont des
-- empreintes différentes, c'est normal, la vérification passe quand même.
--
-- Comptes et mots de passe :
--   user@test … user5@test  ROLE_USER       User@test1
--   admin@test              ROLE_ADMIN      Admin@test1
--   librarian@test          ROLE_LIBRARIAN  Libra@test1
--
-- Tous respectent la règle de RegisterRequest : au moins 8 caractères,
-- une majuscule, une minuscule, un chiffre et un caractère parmi @$!%*?&.
--
-- admin@test et librarian@test portent chacun une réservation : elle sert
-- uniquement à éprouver la tâche d'expiration, isolée des autres cas.
-- Leurs droits d'administration restent leur rôle principal.

-- ============================================================
-- Remise à zéro
--
-- QUOTED_IDENTIFIER ON est obligatoire, pas décoratif : l'index filtré
-- ux_reservation_active impose ce réglage à toute écriture dans la table
-- reservation, y compris un simple DELETE. sqlcmd le laisse à OFF par
-- défaut, d'où l'erreur 1934 si cette ligne manque. Les pilotes JDBC et
-- SSMS le mettent à ON d'eux-mêmes, l'application n'est donc pas concernée.
-- ============================================================

SET QUOTED_IDENTIFIER ON;
GO

DELETE FROM reservation;
DELETE FROM loans;
DELETE FROM review;
DELETE FROM book_genre;
DELETE FROM book_author;
DELETE FROM book_copies;
DELETE FROM refresh_tokens;
DELETE FROM books;
DELETE FROM authors;
DELETE FROM genres;
DELETE FROM users;

-- Les numéros des livres, auteurs et genres sont écrits en clair dans les
-- tables de liaison. Ils sont donc imposés explicitement, via IDENTITY_INSERT,
-- au lieu d'être laissés au compteur.
--
-- DBCC CHECKIDENT(..., RESEED, 0) ne suffisait pas : sur une table où aucune
-- ligne n'a jamais été insérée, le compteur repart de la valeur donnée et non
-- de la valeur suivante — la première ligne recevait l'identifiant 0, et les
-- liaisons pointaient dans le vide. Le réglage explicite est insensible à
-- l'historique de la table.
GO

-- ============================================================
-- Users
-- ============================================================

INSERT INTO users (email, firstname, lastname, password, phone, role, username) VALUES
 (N'user@test',      N'User_firstname',      N'User_lastname',      N'$2a$10$GhV6rIf/xDz57p/lvHYUseGf8AfCXIEVdw5HlUBsXub7IsPtsUxrq', N'0102030400',  N'ROLE_USER',      N'User'),
 (N'user1@test',     N'User_firstname1',     N'User_lastname1',     N'$2a$10$OFPy2rjQR.QV3HrwmcAhKORP.BeoRnSimDFDTyE5RReoY7Yu8Rrxm', N'01020304001', N'ROLE_USER',      N'User1'),
 (N'user2@test',     N'User_firstname2',     N'User_lastname2',     N'$2a$10$RikwO.2.uK3NLHCoLiZIqu5irISbRJ2ZGLd71mPdnF3ZeoQxCHxkW', N'01020304002', N'ROLE_USER',      N'User2'),
 (N'user3@test',     N'User_firstname3',     N'User_lastname3',     N'$2a$10$ZV4.QLwNt7nJkAGTxkmlJOcPMnMygZ9aU86xE/mi86gy5AaS/wwLW', N'01020304003', N'ROLE_USER',      N'User3'),
 (N'user4@test',     N'User_firstname4',     N'User_lastname4',     N'$2a$10$NBSSUsrSfSm91nTvnlx8FujCsRq3rO8xC/96PBw/.izH5a3.bgMU6', N'01020304004', N'ROLE_USER',      N'User4'),
 (N'user5@test',     N'User_firstname5',     N'User_lastname5',     N'$2a$10$pqs8y.bj2ohyliHolnfTeeCIgC0qRtP4GHNQPyC/b2Bd66xpZhhqK', N'01020304005', N'ROLE_USER',      N'User5'),
 (N'admin@test',     N'Admin_firstname',     N'Admin_lastname',     N'$2a$10$SS17vXhwlTkxRVZkT1DCYegY0l.dLf17kosyfNBciAm2MsjQjvgjO', N'01020304006', N'ROLE_ADMIN',     N'Admin'),
 (N'librarian@test', N'Librarian_firstname', N'Librarian_lastname', N'$2a$10$PhW4s9cIdaPoEzlHRaTnDevsVk2jXGQUbDITyeaz8/vb5.4B8oNPK', N'01020304007', N'ROLE_LIBRARIAN', N'Librarian');
GO

-- ============================================================
-- Books
-- ============================================================

SET IDENTITY_INSERT books ON;

INSERT INTO books (id, description, img, isbn, publish_date, title) VALUES
 (1, 'Un jeune sorcier découvre ses pouvoirs et entre dans une école de magie où il devra affronter de nombreuses épreuves.', 'https://example.com/images/harry1.jpg', '9782070643028', '1998-10-09', 'Harry Potter à l''école des sorciers'),
 (2, 'Une aventure fantastique où une communauté se forme pour détruire un anneau maléfique.', 'https://example.com/images/lotr1.jpg', '9782266282366', '1954-07-29', 'La Communauté de l''Anneau'),
 (3, 'Le récit d''une société totalitaire où chaque citoyen est constamment surveillé.', 'https://example.com/images/1984.jpg', '9782070368228', '1949-06-08', '1984'),
 (4, 'Un classique de la littérature racontant la quête de justice d''Edmond Dantès.', 'https://example.com/images/montecristo.jpg', '9782253004223', '1845-08-28', 'Le Comte de Monte-Cristo'),
 (5, 'L''histoire d''un hobbit entraîné malgré lui dans une aventure extraordinaire.', 'https://example.com/images/hobbit.jpg', '9782266282373', '1937-09-21', 'Le Hobbit'),
 (6, 'Une enquête policière où Sherlock Holmes tente de résoudre une mystérieuse affaire.', 'https://example.com/images/holmes.jpg', '9782253006326', '1887-11-01', 'Une étude en rouge'),
 (7, 'Un roman philosophique retraçant le voyage initiatique d''un jeune berger andalou.', 'https://example.com/images/alchimiste.jpg', '9782290004449', '1988-01-01', 'L''Alchimiste'),
 (8, 'Le destin tragique d''une jeune femme confrontée aux illusions de la bourgeoisie.', 'https://example.com/images/bovary.jpg', '9782070409303', '1857-04-01', 'Madame Bovary'),
 (9, 'Une réflexion sur la condition humaine à travers le personnage de Meursault.', 'https://example.com/images/etranger.jpg', '9782070360024', '1942-05-19', 'L''Étranger'),
 (10, 'Une aventure de science-fiction où un équipage explore les profondeurs de l''espace.', 'https://example.com/images/mars.jpg', '9780553418026', '2011-09-27', 'Seul sur Mars'),
 (11, 'Un thriller captivant mêlant symboles religieux et énigmes historiques.', 'https://example.com/images/davinci.jpg', '9782709625210', '2003-03-18', 'Da Vinci Code'),
 (12, 'Une histoire poignante d''amitié entre un milliardaire tétraplégique et son auxiliaire de vie.', 'https://example.com/images/intouchables.jpg', '9782253158575', '2001-05-14', 'Le Second Souffle'),
 (13, 'Un roman d''anticipation où les pompiers brûlent les livres au lieu d''éteindre les incendies.', 'https://example.com/images/f451.jpg', '9782070415731', '1953-10-19', 'Fahrenheit 451'),
 (14, 'Une enquête menée par Hercule Poirot lors d''un voyage à bord d''un train de luxe.', 'https://example.com/images/orient.jpg', '9782253004032', '1934-01-01', 'Le Crime de l''Orient-Express'),
 (15, 'Le témoignage bouleversant d''une jeune fille pendant la Seconde Guerre mondiale.', 'https://example.com/images/annefrank.jpg', '9782253002861', '1947-06-25', 'Le Journal d''Anne Frank'),
 (16, 'Une dystopie dans laquelle les enfants sont sélectionnés pour participer à un jeu mortel.', 'https://example.com/images/hungergames.jpg', '9782266260777', '2008-09-14', 'Hunger Games'),
 (17, 'Une romance historique se déroulant pendant la Seconde Guerre mondiale.', 'https://example.com/images/bookthief.jpg', '9782266211588', '2005-03-14', 'La Voleuse de livres'),
 (18, 'L''histoire d''un prince qui découvre les valeurs essentielles de la vie au fil de ses rencontres.', 'https://example.com/images/petitprince.jpg', '9782070612758', '1943-04-06', 'Le Petit Prince'),
 (19, 'Un détective enquête sur une série de meurtres liés aux sept péchés capitaux.', 'https://example.com/images/seven.jpg', '9782743632106', '1995-09-22', 'Seven'),
 (20, 'Une saga familiale se déroulant dans l''univers des dragons et des royaumes en guerre.', 'https://example.com/images/got.jpg', '9780553103540', '1996-08-06', 'Le Trône de Fer');

SET IDENTITY_INSERT books OFF;
DBCC CHECKIDENT ('books', RESEED);   -- resynchronise le compteur sur le max
GO

-- ============================================================
-- Book copies
--
-- Le service ne considère libre qu'un exemplaire AVAILABLE dont l'état est
-- NEW ou GOOD : existsByBook_IdAndBookStatusAndConditionIn. Un exemplaire
-- WORN ou DAMAGED sur l'étagère ne compte donc pas.
-- Après les retouches du bloc suivant, quatorze livres sont réservables :
--   2, 4, 5, 7, 8, 9, 11, 12, 13, 15, 16, 17, 19, 20
-- et six restent empruntables directement : 1, 3, 6, 10, 14, 18.
-- Le livre 9 est un cas à part : son seul exemplaire exploitable est WORN,
-- il compte donc comme réservable alors qu'il est physiquement sur l'étagère.
-- Quarante-cinq exemplaires en tout.
-- ============================================================

INSERT INTO book_copies (book_status, book_condition, serial_number, book_id) VALUES
 ('AVAILABLE', 'NEW',     'SN000001', 1),
 ('LOANED',    'GOOD',    'SN000002', 2),
 ('AVAILABLE', 'GOOD',    'SN000003', 3),
 ('IN_REPAIR', 'DAMAGED', 'SN000004', 4),
 ('LOST',      'WORN',    'SN000005', 5),
 ('AVAILABLE', 'NEW',     'SN000006', 6),
 ('LOANED',    'GOOD',    'SN000007', 7),
 ('AVAILABLE', 'WORN',    'SN000008', 8),
 ('IN_REPAIR', 'DAMAGED', 'SN000009', 9),
 ('AVAILABLE', 'GOOD',    'SN000010', 10),
 ('LOST',      'DAMAGED', 'SN000011', 11),
 ('AVAILABLE', 'NEW',     'SN000012', 12),
 ('LOANED',    'WORN',    'SN000013', 13),
 ('AVAILABLE', 'GOOD',    'SN000014', 14),
 ('IN_REPAIR', 'WORN',    'SN000015', 15),
 ('AVAILABLE', 'NEW',     'SN000016', 16),
 ('LOANED',    'GOOD',    'SN000017', 17),
 ('AVAILABLE', 'NEW',     'SN000018', 18),
 ('LOST',      'DAMAGED', 'SN000019', 19),
 ('AVAILABLE', 'GOOD',    'SN000020', 20),
 ('LOANED',    'NEW',     'SN000021', 5),
 ('AVAILABLE', 'GOOD',    'SN000022', 8),
 ('IN_REPAIR', 'DAMAGED', 'SN000023', 12),
 ('AVAILABLE', 'WORN',    'SN000024', 1),
 ('LOANED',    'GOOD',    'SN000025', 17),
 ('AVAILABLE', 'NEW',     'SN000026', 3),
 ('LOST',      'DAMAGED', 'SN000027', 10),
 ('AVAILABLE', 'GOOD',    'SN000028', 14),
 ('IN_REPAIR', 'WORN',    'SN000029', 19),
 ('AVAILABLE', 'NEW',     'SN000030', 6),
 ('LOANED',    'GOOD',    'SN000031', 2),
 ('AVAILABLE', 'WORN',    'SN000032', 9),
 ('LOST',      'DAMAGED', 'SN000033', 15),
 ('AVAILABLE', 'GOOD',    'SN000034', 11),
 ('IN_REPAIR', 'NEW',     'SN000035', 20),
 ('AVAILABLE', 'GOOD',    'SN000036', 13),
 ('LOANED',    'WORN',    'SN000037', 4),
 ('AVAILABLE', 'NEW',     'SN000038', 7),
 ('LOST',      'GOOD',    'SN000039', 16),
 ('AVAILABLE', 'DAMAGED', 'SN000040', 18),
 -- Cinq exemplaires supplémentaires, tous prêtés à user4@test. Ils portent
 -- sur des livres déjà indisponibles : ajouter un exemplaire prêté ne rend
 -- aucun livre empruntable, l'équilibre du catalogue reste inchangé.
 ('LOANED',    'GOOD',    'SN000041', 4),
 ('LOANED',    'NEW',     'SN000042', 7),
 ('LOANED',    'GOOD',    'SN000043', 8),
 ('LOANED',    'NEW',     'SN000044', 13),
 ('LOANED',    'GOOD',    'SN000045', 19);
GO

-- ============================================================
-- Authors
-- ============================================================

SET IDENTITY_INSERT authors ON;

INSERT INTO authors (id, firstname, lastname, pen_name) VALUES
 (1, 'Victor',    'Hugo',      'Victor Hugo'),
 (2, 'Jules',     'Verne',     'Jules Verne'),
 (3, 'Agatha',    'Christie',  'A. Christie'),
 (4, 'George',    'Orwell',    'George Orwell'),
 (5, 'Jane',      'Austen',    'Jane Austen'),
 (6, 'Arthur',    'Doyle',     'A. C. Doyle'),
 (7, 'Isaac',     'Asimov',    'Isaac Asimov'),
 (8, 'Stephen',   'King',      'Richard Bachman'),
 (9, 'Joanne',    'Rowling',   'J. K. Rowling'),
 (10, 'Ernest',    'Hemingway', 'Ernest Hemingway'),
 (11, 'Alexandre', 'Dumas',     'A. Dumas'),
 (12, 'Mary',      'Shelley',   'Mary Shelley'),
 (13, 'Edgar',     'Poe',       'Edgar Poe'),
 (14, 'Herman',    'Melville',  'H. Melville'),
 (15, 'Charles',   'Dickens',   'Boz'),
 (16, 'Margaret',  'Atwood',    'Margaret Atwood'),
 (17, 'Haruki',    'Murakami',  'Haruki Murakami'),
 (18, 'Albert',    'Camus',     'Albert Camus'),
 (19, 'Umberto',   'Eco',       'Umberto Eco'),
 (20, 'Neil',      'Gaiman',    'Neil Gaiman');

SET IDENTITY_INSERT authors OFF;
DBCC CHECKIDENT ('authors', RESEED);   -- resynchronise le compteur sur le max
GO

-- ============================================================
-- Liaison livre / auteur
-- ============================================================

INSERT INTO book_author (book_id, author_id) VALUES
 (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),
 (11,11),(12,12),(13,13),(14,14),(15,15),(16,16),(17,17),(18,18),(19,19),(20,20),
 (2,7),(5,12),(8,20),(10,3),(13,6),(15,1),(18,9),(20,17);
GO

-- ============================================================
-- Genres
-- ============================================================

SET IDENTITY_INSERT genres ON;

INSERT INTO genres (id, label) VALUES
 (1, 'Science-Fiction'),
 (2, 'Fantasy'),
 (3, 'Dystopie'),
 (4, 'Thriller'),
 (5, 'Policier'),
 (6, 'Roman historique'),
 (7, 'Aventure'),
 (8, 'Horreur'),
 (9, 'Romance'),
 (10, 'Biographie');

SET IDENTITY_INSERT genres OFF;
DBCC CHECKIDENT ('genres', RESEED);   -- resynchronise le compteur sur le max
GO

-- ============================================================
-- Liaison livre / genre
-- ============================================================

INSERT INTO book_genre (book_id, genre_id) VALUES
 (1,1),(1,3),
 (2,7),(2,1),
 (3,5),(3,4),
 (4,3),
 (5,2),(5,7),
 (6,5),
 (7,1),(7,2),
 (8,8),(8,4),
 (9,2),(9,9),
 (10,10),
 (11,6),(11,7),
 (12,8),(12,1),
 (13,4),(13,5),
 (14,7),
 (15,6),(15,9),
 (16,3),(16,1),(16,4),
 (17,2),
 (18,6),(18,10),
 (19,9),(19,6),
 (20,1),(20,2),(20,7);
GO

-- ============================================================
-- Cohérence des exemplaires avant les prêts et réservations
--
--   SN000021 (livre 5)  prêté  -> RESERVED   mis de côté, réservation en cours
--   SN000025 (livre 17) prêté  -> RESERVED   mis de côté, réservation en cours
--   SN000034 (livre 11) libre  -> RESERVED   mis de côté, échéance dépassée
--   SN000020 (livre 20) libre  -> RESERVED   mis de côté pour user2@test, qui est en retard
--   SN000032 (livre 9)  libre  -> RESERVED   mis de côté mais WORN : retrait refusé
--   SN000016 (livre 16) libre  -> RESERVED   mis de côté pour user4@test, au plafond
--   SN000012 (livre 12) libre  -> RESERVED   mis de côté, échéance dans 90 secondes
--   SN000028 (livre 14) libre  -> RESERVED   ORPHELIN : aucune réservation
--   SN000036 (livre 13) libre  -> LOANED     troisième prêt de user1@test
--   SN000022 (livre 8)  libre  -> LOANED     prêt en retard de user2@test
--   SN000038 (livre 7)  libre  -> IN_REPAIR  ferme le livre 7
--
-- Les cinq prêts de user4@test ne passent pas par ce bloc : ils portent sur
-- des exemplaires ajoutés directement au statut LOANED (SN000041 à SN000045),
-- sur des livres déjà indisponibles. Retourner un exemplaire libre vers un
-- prêt aurait rendu six livres de plus réservables et vidé le catalogue
-- empruntable.
--
-- SN000032 est le seul exemplaire exploitable du livre 9 et il est WORN.
-- Mis de côté, il permet d'atteindre le dernier refus de fulfillIfReady :
-- « L'exemplaire réservé n'est plus dans un état empruntable ».
--
-- Un exemplaire mis de côté ne peut pas être prêté en même temps :
-- d'où les deux premières lignes.
--
-- SN000028 est volontairement incohérent : un exemplaire RESERVED que
-- plus aucune réservation ne réclame. Aucun code ne le rattrapera :
-- promote() n'est appelé qu'avec l'exemplaire porté par une réservation,
-- et aucune ne pointe sur celui-ci. La ligne sert à l'inspection — voir
-- qu'une base peut contenir ce genre de résidu — pas à exécuter un chemin.
-- Le livre 14 garde un autre exemplaire libre, il reste empruntable.
--
-- SN000038 ferme le livre 7, où user@test a déjà un prêt : c'est le seul
-- moyen d'atteindre le refus « vous empruntez déjà ce livre » sans que le
-- contrôle « un exemplaire est libre » ne se déclenche avant.
-- ============================================================

UPDATE book_copies SET book_status = 'RESERVED'  WHERE serial_number IN ('SN000021','SN000025','SN000034','SN000020','SN000032','SN000016','SN000012','SN000028');
UPDATE book_copies SET book_status = 'LOANED'    WHERE serial_number IN ('SN000036','SN000022');
UPDATE book_copies SET book_status = 'IN_REPAIR' WHERE serial_number = 'SN000038';
GO

-- ============================================================
-- Prêts
--
-- loan_date, due_date et return_date sont des DATE : GETDATE(),
-- pas SYSUTCDATETIME(). Durée de 14 jours partout (RG-LOAN-02).
--
-- Un prêt actif existe pour chacun des huit exemplaires LOANED.
-- Aucun prêt ne porte sur un exemplaire RESERVED.
--
-- Les prêts RETURNED servent deux rôles : l'historique du lecteur,
-- et le droit de déposer un avis (voir le bloc « Avis »).
--
-- Aucun emprunteur du livre 2 ne figure dans sa file d'attente :
-- l'application refuserait cette combinaison (contrôle RG-RESA-04).
-- ============================================================

DECLARE @c2  BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000002');
DECLARE @c7  BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000007');
DECLARE @c13 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000013');
DECLARE @c17 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000017');
DECLARE @c31 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000031');
DECLARE @c36 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000036');
DECLARE @c37 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000037');
DECLARE @c22 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000022');
DECLARE @c1  BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000001');
DECLARE @c3  BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000003');
DECLARE @c24 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000024');
DECLARE @c6  BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000006');
DECLARE @c10 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000010');
DECLARE @c41 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000041');
DECLARE @c42 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000042');
DECLARE @c43 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000043');
DECLARE @c44 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000044');
DECLARE @c45 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000045');

DECLARE @u1 BIGINT = (SELECT id FROM users WHERE email = 'user@test');
DECLARE @u2 BIGINT = (SELECT id FROM users WHERE email = 'user1@test');
DECLARE @u3 BIGINT = (SELECT id FROM users WHERE email = 'user2@test');
DECLARE @u4 BIGINT = (SELECT id FROM users WHERE email = 'user3@test');
DECLARE @u5 BIGINT = (SELECT id FROM users WHERE email = 'user4@test');
DECLARE @u6 BIGINT = (SELECT id FROM users WHERE email = 'user5@test');

INSERT INTO loans (loan_date, due_date, return_date, status, book_copy_loaned_id, loaner_id) VALUES
 -- LE PRÊT DE LA DÉMONSTRATION. Livre 2, aucun exemplaire libre, trois lecteurs
 -- en attente. C'est son retour qui fera avancer la file.
 (DATEADD(day,-10, CAST(GETDATE() AS DATE)), DATEADD(day,  4, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c2,  @u4),

 -- Retard : due_date dépassée de six jours. Second exemplaire du livre 2.
 (DATEADD(day,-20, CAST(GETDATE() AS DATE)), DATEADD(day, -6, CAST(GETDATE() AS DATE)), NULL, 'OVERDUE', @c31, @u6),

 -- user@test détient le livre 7, désormais sans exemplaire libre :
 -- tenter de le réserver donne « vous empruntez déjà ce livre ».
 (DATEADD(day, -3, CAST(GETDATE() AS DATE)), DATEADD(day, 11, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c7,  @u1),

 -- Prêt né d'une réservation honorée : voir la ligne FULFILLED plus bas.
 (DATEADD(day, -5, CAST(GETDATE() AS DATE)), DATEADD(day,  9, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c13, @u1),

 -- user1@test a trois emprunts actifs, le plafond fixé par RG-LOAN-01.
 -- Attention en testant : le code compare aujourd'hui à 5 et non à 3,
 -- donc le refus ne se déclenchera pas. L'écart est volontairement visible.
 (DATEADD(day, -7, CAST(GETDATE() AS DATE)), DATEADD(day,  7, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c17, @u2),
 (DATEADD(day, -2, CAST(GETDATE() AS DATE)), DATEADD(day, 12, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c37, @u2),
 (DATEADD(day, -1, CAST(GETDATE() AS DATE)), DATEADD(day, 13, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c36, @u2),

 -- RG-LOAN-03 : user2@test a un prêt en retard sur le livre 8, et une
 -- réservation prête sur le livre 20 dont l'échéance est encore loin.
 -- Son clic sur « Prendre » doit être refusé tant qu'il n'a pas rendu.
 -- Le contrôle n'existe pas encore dans le code : le retrait passera,
 -- et c'est précisément ce que ce jeu doit rendre visible.
 (DATEADD(day,-25, CAST(GETDATE() AS DATE)), DATEADD(day,-11, CAST(GETDATE() AS DATE)), NULL, 'OVERDUE', @c22, @u3),

 -- Historique : rendu dans les temps, puis rendu avec quatre jours de retard.
 (DATEADD(day,-40, CAST(GETDATE() AS DATE)), DATEADD(day,-26, CAST(GETDATE() AS DATE)), DATEADD(day,-28, CAST(GETDATE() AS DATE)), 'RETURNED', @c1, @u1),
 (DATEADD(day,-35, CAST(GETDATE() AS DATE)), DATEADD(day,-21, CAST(GETDATE() AS DATE)), DATEADD(day,-17, CAST(GETDATE() AS DATE)), 'RETURNED', @c3, @u3),

 -- PLAFOND D'EMPRUNTS TEL QUE LE CODE LE COMPTE — user4@test tient cinq
 -- prêts actifs sur cinq livres distincts. Le contrôle « >= 5 » de
 -- createLoan se déclenche donc réellement, et son « Prendre » sur la
 -- réservation du livre 16 sera refusé. Aucun retard chez lui, et
 -- l'exemplaire mis de côté est NEW : rien d'autre ne peut masquer ce refus.
 -- À lire avec user1@test, resté à trois prêts : côte à côte, les deux
 -- montrent l'écart entre la règle RG-LOAN-01 et le seuil codé.
 (DATEADD(day,-12, CAST(GETDATE() AS DATE)), DATEADD(day,  2, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c41, @u5),
 (DATEADD(day, -9, CAST(GETDATE() AS DATE)), DATEADD(day,  5, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c42, @u5),
 (DATEADD(day, -6, CAST(GETDATE() AS DATE)), DATEADD(day,  8, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c43, @u5),
 (DATEADD(day, -4, CAST(GETDATE() AS DATE)), DATEADD(day, 10, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c44, @u5),
 (DATEADD(day, -1, CAST(GETDATE() AS DATE)), DATEADD(day, 13, CAST(GETDATE() AS DATE)), NULL, 'ACTIVE',  @c45, @u5),

 -- Prêts rendus qui ouvrent le droit à un avis : createReview exige
 -- un prêt RETURNED sur le livre noté. Sans eux, le module d'avis
 -- ne se teste pas — tout se solderait par « Vous ne pouvez pas noter ».
 --   user4@test / livre 1  : deuxième avis sur le même livre que user@test
 --   user1@test / livre 6  : avis simple
 --   user3@test / livre 10 : aucun avis encore, sert au cas nominal de création
 (DATEADD(day,-60, CAST(GETDATE() AS DATE)), DATEADD(day,-46, CAST(GETDATE() AS DATE)), DATEADD(day,-48, CAST(GETDATE() AS DATE)), 'RETURNED', @c24, @u5),
 (DATEADD(day,-55, CAST(GETDATE() AS DATE)), DATEADD(day,-41, CAST(GETDATE() AS DATE)), DATEADD(day,-43, CAST(GETDATE() AS DATE)), 'RETURNED', @c6,  @u2),
 (DATEADD(day,-50, CAST(GETDATE() AS DATE)), DATEADD(day,-36, CAST(GETDATE() AS DATE)), DATEADD(day,-38, CAST(GETDATE() AS DATE)), 'RETURNED', @c10, @u4),

 -- Quatre prêts rendus de plus sur le livre 1, pour porter à six le nombre
 -- d'avis qu'il peut recevoir : la contrainte d'unicité (users_id, book_id)
 -- limite à un avis par lecteur, il faut donc six lecteurs différents.
 -- Les deux exemplaires du livre 1 sont réutilisés dans le temps, sans
 -- chevauchement : un exemplaire ne peut pas être prêté deux fois à la fois.
 --   SN000001 : user1@test, puis user3@test, puis user@test
 --   SN000024 : user2@test, puis user5@test, puis user4@test
 (DATEADD(day,-90, CAST(GETDATE() AS DATE)), DATEADD(day,-76, CAST(GETDATE() AS DATE)), DATEADD(day,-78, CAST(GETDATE() AS DATE)), 'RETURNED', @c1,  @u2),
 (DATEADD(day,-85, CAST(GETDATE() AS DATE)), DATEADD(day,-71, CAST(GETDATE() AS DATE)), DATEADD(day,-73, CAST(GETDATE() AS DATE)), 'RETURNED', @c24, @u3),
 (DATEADD(day,-75, CAST(GETDATE() AS DATE)), DATEADD(day,-61, CAST(GETDATE() AS DATE)), DATEADD(day,-63, CAST(GETDATE() AS DATE)), 'RETURNED', @c1,  @u4),
 (DATEADD(day,-71, CAST(GETDATE() AS DATE)), DATEADD(day,-57, CAST(GETDATE() AS DATE)), DATEADD(day,-62, CAST(GETDATE() AS DATE)), 'RETURNED', @c24, @u6);
GO

-- ============================================================
-- Réservations
--
-- reserves_date, notified_at et pickup_deadline sont écrits en UTC
-- par Java : SYSUTCDATETIME(), pas GETDATE().
--
-- Ce que couvre le jeu :
--   file de trois personnes sur le livre 2, menée par user@test
--   décompte calme      : 70h restantes
--   décompte urgent     : 4h restantes, affichage rouge
--   échéance dépassée   : la tâche planifiée doit la passer en EXPIRED
--                         et promouvoir la personne suivante
--   les cinq statuts, chacun au moins une fois
--   un lecteur au plafond de cinq réservations actives
--
-- Les refus de fulfillIfReady, un par lecteur pour qu'ils ne se masquent
-- pas les uns les autres :
--   plafond tel que codé   user4@test, livre 16  -> refus effectif (5 prêts)
--   plafond de la règle    user1@test, livre 5   -> aucun refus (3 prêts)
--   emprunt en retard      user2@test, livre 20  -> contrôle absent du code
--   exemplaire abîmé       user@test,  livre 9   -> refus effectif
--   délai dépassé          voir les deux lignes de la tâche planifiée
-- Le retrait qui aboutit : user@test sur le livre 17.
--
-- La tâche planifiée est éprouvée deux fois, sur les comptes d'exploitation
-- qui n'ont aucun autre rôle ici :
--   admin@test,     livre 11 : échéance dépassée d'une heure -> état d'après
--   librarian@test, livre 12 : échéance dans 90 secondes     -> bascule en direct
--
-- ATTENTION : la tâche planifiée tourne toutes les minutes. La ligne du
-- livre 11 sera consommée dès le premier passage. Celle du livre 12 laisse
-- 90 secondes : c'est la fenêtre pour ouvrir l'écran et voir le décompte
-- s'achever, la réservation passer en EXPIRED et user2@test être promu.
-- ============================================================

DECLARE @b2  BIGINT = (SELECT id FROM books WHERE isbn = '9782266282366');  -- La Communauté de l'Anneau
DECLARE @b4  BIGINT = (SELECT id FROM books WHERE isbn = '9782253004223');  -- Le Comte de Monte-Cristo
DECLARE @b5  BIGINT = (SELECT id FROM books WHERE isbn = '9782266282373');  -- Le Hobbit
DECLARE @b7  BIGINT = (SELECT id FROM books WHERE isbn = '9782290004449');  -- L'Alchimiste
DECLARE @b9  BIGINT = (SELECT id FROM books WHERE isbn = '9782070360024');  -- L'Étranger
DECLARE @b11 BIGINT = (SELECT id FROM books WHERE isbn = '9782709625210');  -- Da Vinci Code
DECLARE @b13 BIGINT = (SELECT id FROM books WHERE isbn = '9782070415731');  -- Fahrenheit 451
DECLARE @b15 BIGINT = (SELECT id FROM books WHERE isbn = '9782253002861');  -- Le Journal d'Anne Frank
DECLARE @b17 BIGINT = (SELECT id FROM books WHERE isbn = '9782266211588');  -- La Voleuse de livres
DECLARE @b19 BIGINT = (SELECT id FROM books WHERE isbn = '9782743632106');  -- Seven

DECLARE @b12 BIGINT = (SELECT id FROM books WHERE isbn = '9782253158575');  -- Le Second Souffle
DECLARE @b16 BIGINT = (SELECT id FROM books WHERE isbn = '9782266260777');  -- Hunger Games
DECLARE @b20 BIGINT = (SELECT id FROM books WHERE isbn = '9780553103540');  -- Le Trône de Fer

DECLARE @r21 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000021');
DECLARE @r25 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000025');
DECLARE @r34 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000034');
DECLARE @r13 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000013');
DECLARE @r20 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000020');
DECLARE @r32 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000032');
DECLARE @r16 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000016');
DECLARE @r12 BIGINT = (SELECT id FROM book_copies WHERE serial_number = 'SN000012');

DECLARE @v1 BIGINT = (SELECT id FROM users WHERE email = 'user@test');
DECLARE @v2 BIGINT = (SELECT id FROM users WHERE email = 'user1@test');
DECLARE @v3 BIGINT = (SELECT id FROM users WHERE email = 'user2@test');
DECLARE @v4 BIGINT = (SELECT id FROM users WHERE email = 'user3@test');
DECLARE @v5 BIGINT = (SELECT id FROM users WHERE email = 'user4@test');
DECLARE @v6 BIGINT = (SELECT id FROM users WHERE email = 'user5@test');
DECLARE @v7 BIGINT = (SELECT id FROM users WHERE email = 'admin@test');
DECLARE @v8 BIGINT = (SELECT id FROM users WHERE email = 'librarian@test');

INSERT INTO reservation (reserves_date, notified_at, pickup_deadline, reservation_status, book_id, book_copy_id, users_id) VALUES

 -- FILE D'ATTENTE — livre 2. L'ordre vient de reserves_date.
 -- user@test est premier : le retour de SN000002 le fera passer
 -- en READY_FOR_PICKUP avec 72h au compteur.
 (DATEADD(day, -3, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b2, NULL, @v1),
 (DATEADD(day, -2, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b2, NULL, @v3),
 (DATEADD(day, -1, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b2, NULL, @v5),

 -- DÉCOMPTE CALME — notifié il y a 2h, 70h restantes. Affichage sobre.
 (DATEADD(day, -4, SYSUTCDATETIME()), DATEADD(hour,  -2, SYSUTCDATETIME()), DATEADD(hour, 70, SYSUTCDATETIME()), 'READY_FOR_PICKUP', @b17, @r25, @v1),

 -- DÉCOMPTE URGENT — 4h restantes, affichage rouge.
 -- user1@test a trois emprunts actifs : d'après RG-LOAN-01 son « Prendre »
 -- doit être refusé. Le code compare à 5, le refus ne viendra donc pas :
 -- l'écart est volontairement laissé visible.
 (DATEADD(day, -6, SYSUTCDATETIME()), DATEADD(hour, -68, SYSUTCDATETIME()), DATEADD(hour,  4, SYSUTCDATETIME()), 'READY_FOR_PICKUP', @b5,  @r21, @v2),

 -- RETRAIT REFUSÉ POUR CAUSE DE RETARD — user2@test traîne le livre 8
 -- depuis onze jours. Échéance lointaine : le refus attendu porte sur
 -- le retard, pas sur le délai. Aujourd'hui le retrait passera quand même.
 (DATEADD(day, -1, SYSUTCDATETIME()), DATEADD(hour,  -6, SYSUTCDATETIME()), DATEADD(hour, 66, SYSUTCDATETIME()), 'READY_FOR_PICKUP', @b20, @r20, @v3),

 -- RETRAIT REFUSÉ POUR CAUSE D'ÉTAT — l'exemplaire mis de côté est WORN.
 -- Dernier refus de fulfillIfReady, atteint seulement si le lecteur n'est
 -- ni en retard ni au plafond : user@test remplit ces deux conditions.
 (DATEADD(day, -2, SYSUTCDATETIME()), DATEADD(hour,  -8, SYSUTCDATETIME()), DATEADD(hour, 64, SYSUTCDATETIME()), 'READY_FOR_PICKUP', @b9,  @r32, @v1),

 -- RETRAIT REFUSÉ POUR CAUSE DE PLAFOND — user4@test tient cinq prêts,
 -- seuil que le code applique réellement. Échéance lointaine, exemplaire NEW,
 -- aucun retard : le refus ne peut venir que du plafond.
 (DATEADD(day, -1, SYSUTCDATETIME()), DATEADD(hour, -22, SYSUTCDATETIME()), DATEADD(hour, 50, SYSUTCDATETIME()), 'READY_FOR_PICKUP', @b16, @r16, @v5),

 -- ÉCHÉANCE DÉJÀ DÉPASSÉE D'UNE HEURE — sera consommée par la tâche dès son
 -- premier passage : on observe l'état d'après, EXPIRED et file avancée.
 -- Portée par admin@test, qui n'a aucun autre rôle dans ce jeu : la tâche
 -- planifiée est ainsi isolée des quatre refus de retrait.
 (DATEADD(day, -7, SYSUTCDATETIME()), DATEADD(hour, -73, SYSUTCDATETIME()), DATEADD(hour, -1, SYSUTCDATETIME()), 'READY_FOR_PICKUP', @b11, @r34, @v7),
 (DATEADD(day, -4, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b11, NULL, @v4),

 -- ÉCHÉANCE DANS 90 SECONDES — l'autre moitié du même essai. Charger le jeu,
 -- ouvrir la réservation, regarder le décompte finir : on voit la bascule
 -- se produire au lieu de constater qu'elle a eu lieu. user2@test attend
 -- derrière et doit être promu dans la foulée, avec 72h neuves.
 (DATEADD(day, -3, SYSUTCDATETIME()), DATEADD(hour, -72, SYSUTCDATETIME()), DATEADD(second, 90, SYSUTCDATETIME()), 'READY_FOR_PICKUP', @b12, @r12, @v8),
 (DATEADD(day, -2, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b12, NULL, @v3),

 -- EXPIRED — le même exemplaire SN000025 avait d'abord été proposé à
 -- user2@test, qui n'est pas venu ; il est ensuite passé à user@test.
 (DATEADD(day,-10, SYSUTCDATETIME()), DATEADD(day, -6, SYSUTCDATETIME()), DATEADD(day, -3, SYSUTCDATETIME()), 'EXPIRED', @b17, @r25, @v3),

 -- CANCELLED — renoncement depuis la file, aucun exemplaire n'était engagé.
 (DATEADD(day, -6, SYSUTCDATETIME()), NULL, NULL, 'CANCELLED', @b19, NULL, @v5),

 -- FULFILLED — devenue le prêt du livre 13 par user@test.
 (DATEADD(day,-12, SYSUTCDATETIME()), DATEADD(day, -6, SYSUTCDATETIME()), DATEADD(day, -3, SYSUTCDATETIME()), 'FULFILLED', @b13, @r13, @v1),

 -- PLAFOND — user5@test a cinq réservations actives, sur cinq livres distincts
 -- et tous réservables. Volontairement hors du livre 2 : la file de la
 -- démonstration doit rester menée par user@test.
 (DATEADD(day, -5, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b4,  NULL, @v6),
 (DATEADD(day, -5, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b7,  NULL, @v6),
 (DATEADD(day, -5, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b9,  NULL, @v6),
 (DATEADD(day, -5, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b15, NULL, @v6),
 (DATEADD(day, -5, SYSUTCDATETIME()), NULL, NULL, 'PENDING', @b19, NULL, @v6);
GO

-- ============================================================
-- Avis
--
-- createReview exige un prêt RETURNED sur le livre noté : chaque ligne
-- ci-dessous s'appuie sur un prêt rendu du bloc précédent.
-- La table porte une contrainte d'unicité (users_id, book_id).
--
-- Ce que couvre le jeu :
--   livre 1  : deux avis, de user@test et user4@test
--              -> liste paginée, et refus « avis d'un autre utilisateur »
--              -> user@test a déjà un avis : un second donne 409
--   livre 3  : avis MODERATED de user2@test
--              -> refus de modification et de suppression
--              -> point de départ de l'écran d'administration
--   livre 6  : avis ACTIVE de user1@test, cible de la modération admin
--   livre 10 : prêt rendu par user3@test SANS avis
--              -> cas nominal de création
--   livre 2  : aucun lecteur ne l'a rendu -> refus « Vous ne pouvez pas noter »
--
-- created_at est écrit ici en UTC. Attention : l'entité le renseigne via
-- @PrePersist avec LocalDateTime.now(), c'est-à-dire l'heure de la JVM.
-- Les avis créés par l'application seront donc décalés de ceux-ci.
-- ============================================================

DECLARE @a1  BIGINT = (SELECT id FROM books WHERE isbn = '9782070643028');  -- Harry Potter
DECLARE @a3  BIGINT = (SELECT id FROM books WHERE isbn = '9782070368228');  -- 1984
DECLARE @a6  BIGINT = (SELECT id FROM books WHERE isbn = '9782253006326');  -- Une étude en rouge

DECLARE @w1 BIGINT = (SELECT id FROM users WHERE email = 'user@test');
DECLARE @w2 BIGINT = (SELECT id FROM users WHERE email = 'user1@test');
DECLARE @w3 BIGINT = (SELECT id FROM users WHERE email = 'user2@test');
DECLARE @w4 BIGINT = (SELECT id FROM users WHERE email = 'user3@test');
DECLARE @w5 BIGINT = (SELECT id FROM users WHERE email = 'user4@test');
DECLARE @w6 BIGINT = (SELECT id FROM users WHERE email = 'user5@test');

INSERT INTO review (rating, comment, status, users_id, book_id, created_at, updated_at, moderated_at) VALUES
 -- Livre 1, premier avis. user@test a rendu SN000001 il y a 28 jours.
 (5, N'Une entrée en matière irrésistible, lue d''une traite.', 'ACTIVE', @w1, @a1,
     DATEADD(day,-27, SYSUTCDATETIME()), NULL, NULL),

 -- Livre 1, second avis. Sert de cible au refus « avis d''un autre utilisateur » :
 -- se connecter en user@test et tenter de le modifier.
 (3, N'Sympathique, mais je n''ai pas retrouvé l''enthousiasme général.', 'ACTIVE', @w5, @a1,
     DATEADD(day,-40, SYSUTCDATETIME()), DATEADD(day,-38, SYSUTCDATETIME()), NULL),

 -- Livre 3, avis modéré. Son auteur ne peut plus ni le modifier ni le supprimer.
 (2, N'Commentaire retiré par la modération.', 'MODERATED', @w3, @a3,
     DATEADD(day,-16, SYSUTCDATETIME()), NULL, DATEADD(day,-12, SYSUTCDATETIME())),

 -- Livre 6, avis actif : cible de PATCH /api/admin/reviews/{id}/moderate.
 (4, N'Une enquête courte et bien menée, idéale pour découvrir la série.', 'ACTIVE', @w2, @a6,
     DATEADD(day,-35, SYSUTCDATETIME()), NULL, NULL),

 -- Quatre avis de plus sur le livre 1 : six au total, de six lecteurs.
 -- La taille de page par défaut est de 20, donc pour éprouver la pagination
 -- il faut la forcer : GET /api/books/1/reviews?size=2 rend trois pages.
 -- Notes volontairement dispersées, de 1 à 5, pour vérifier l'affichage
 -- de la moyenne et le tri éventuel.
 (4, N'Un classique du genre, je comprends l''engouement.', 'ACTIVE', @w2, @a1,
     DATEADD(day,-77, SYSUTCDATETIME()), NULL, NULL),
 (1, N'Trop long, trop lent, je n''ai pas accroché du tout.', 'ACTIVE', @w3, @a1,
     DATEADD(day,-72, SYSUTCDATETIME()), NULL, NULL),
 (5, N'Relu trois fois, toujours le même plaisir.', 'ACTIVE', @w4, @a1,
     DATEADD(day,-62, SYSUTCDATETIME()), DATEADD(day,-60, SYSUTCDATETIME()), NULL),
 (2, N'Correct sans plus, la fin m''a laissé de marbre.', 'ACTIVE', @w6, @a1,
     DATEADD(day,-57, SYSUTCDATETIME()), NULL, NULL);
GO
