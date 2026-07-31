USE [BookHub]
GO

/*
 Jeu de données de test pour BookHub
 25 livres, 22 auteurs, 8 genres, 32 exemplaires, 15 utilisateurs
 Les statuts des exemplaires, emprunts et réservations sont cohérents entre eux :
 - un exemplaire LOANED a toujours un emprunt ACTIVE ou OVERDUE en cours
 - un exemplaire RESERVED a toujours une réservation READY_FOR_PICKUP ou PENDING en cours
 - les emprunts RETURNED portent sur des exemplaires actuellement AVAILABLE
 - les avis (review) ne concernent que des livres déjà empruntés et rendus
 Date de référence utilisée pour les calculs de retard : 2026-07-31
*/

-- ============================================================
-- GENRES
-- ============================================================
SET IDENTITY_INSERT [dbo].[genres] ON
GO
INSERT INTO [dbo].[genres] ([id], [label]) VALUES
(1, 'Fiction'),
(2, 'Science-Fiction'),
(3, 'Fantasy'),
(4, 'Policier'),
(5, 'Romance'),
(6, 'Biographie'),
(7, 'Histoire'),
(8, 'Informatique')
GO
SET IDENTITY_INSERT [dbo].[genres] OFF
GO

-- ============================================================
-- AUTHORS
-- ============================================================
SET IDENTITY_INSERT [dbo].[authors] ON
GO
INSERT INTO [dbo].[authors] ([id], [firstname], [lastname], [pen_name]) VALUES
(1, 'Victor', 'Hugo', NULL),
(2, 'Jules', 'Verne', NULL),
(3, NULL, NULL, 'George Orwell'),
(4, 'Agatha', 'Christie', NULL),
(5, 'Isaac', 'Asimov', NULL),
(6, 'Joanne', 'Rowling', 'J.K. Rowling'),
(7, 'Albert', 'Camus', NULL),
(8, 'Franz', 'Kafka', NULL),
(9, 'Stephen', 'King', NULL),
(10, NULL, NULL, 'Molière'),
(11, 'Marguerite', 'Duras', NULL),
(12, 'Michel', 'Houellebecq', NULL),
(13, 'Amélie', 'Nothomb', NULL),
(14, 'Robert', 'Martin', NULL),
(15, 'Andrew', 'Hunt', NULL),
(16, 'Aldous', 'Huxley', NULL),
(17, 'Ray', 'Bradbury', NULL),
(18, 'John Ronald', 'Tolkien', 'J.R.R. Tolkien'),
(19, 'Émile', 'Zola', NULL),
(20, 'Gustave', 'Flaubert', NULL),
(21, 'Antoine', 'de Saint-Exupéry', NULL),
(22, 'Martin', 'Fowler', NULL)
GO
SET IDENTITY_INSERT [dbo].[authors] OFF
GO

-- ============================================================
-- BOOKS
-- ============================================================
SET IDENTITY_INSERT [dbo].[books] ON
GO
INSERT INTO [dbo].[books] ([id], [description], [img], [isbn], [publish_date], [title]) VALUES
(1, 'Un classique de la littérature française sur la rédemption et la justice sociale.', NULL, '9782200000001', '1862-01-01', 'Les Misérables'),
(2, 'Une aventure fantastique sous les mers à bord du Nautilus.', NULL, '9782200000002', '1870-01-01', 'Vingt mille lieues sous les mers'),
(3, 'Une dystopie glaçante sur la surveillance totalitaire.', NULL, '9782200000003', '1949-06-08', '1984'),
(4, 'Une enquête menée par le célèbre détective belge Hercule Poirot.', NULL, '9782200000004', '1934-01-01', 'Le Crime de l''Orient-Express'),
(5, 'Le premier tome du cycle Fondation, une saga de science-fiction majeure.', NULL, '9782200000005', '1951-05-01', 'Fondation'),
(6, 'Les débuts d''un jeune sorcier à l''école de Poudlard.', NULL, '9782200000006', '1997-06-26', 'Harry Potter à l''école des sorciers'),
(7, 'Une réflexion sur l''absurdité de l''existence.', NULL, '9782200000007', '1942-01-01', 'L''Étranger'),
(8, 'Un homme se réveille métamorphosé en insecte géant.', NULL, '9782200000008', '1915-01-01', 'La Métamorphose'),
(9, 'Un roman d''horreur autour d''une ville hantée par un clown maléfique.', NULL, '9782200000009', '1986-09-15', 'Ça'),
(10, 'Une comédie satirique sur l''hypocrisie de la bourgeoisie.', NULL, '9782200000010', '1664-01-01', 'Tartuffe'),
(11, 'Le récit d''un amour impossible en Indochine.', NULL, '9782200000011', '1984-01-01', 'L''Amant'),
(12, 'Une critique acerbe de la société contemporaine et du tourisme sexuel.', NULL, '9782200000012', '2001-01-01', 'Plateforme'),
(13, 'Le témoignage d''une salariée confrontée au monde du travail japonais.', NULL, '9782200000013', '1992-01-01', 'Stupeur et Tremblements'),
(14, 'Un guide de référence sur l''écriture de code propre et maintenable.', NULL, '9782200000014', '2008-08-01', 'Clean Code'),
(15, 'Un ensemble de conseils pragmatiques pour devenir un meilleur développeur.', NULL, '9782200000015', '1999-10-30', 'The Pragmatic Programmer'),
(16, 'La suite des aventures du jeune sorcier, désormais en deuxième année.', NULL, '9782200000016', '1998-07-02', 'Harry Potter et la Chambre des secrets'),
(17, 'Une nouvelle enquête d''Hercule Poirot sur les rives du Nil.', NULL, '9782200000017', '1937-01-01', 'Mort sur le Nil'),
(18, 'Une contre-utopie sur une société modelée par le contrôle biologique.', NULL, '9782200000018', '1932-01-01', 'Le Meilleur des mondes'),
(19, 'Une société où les livres sont interdits et brûlés.', NULL, '9782200000019', '1953-01-01', 'Fahrenheit 451'),
(20, 'Une quête épique à travers la Terre du Milieu.', NULL, '9782200000020', '1954-07-29', 'Le Seigneur des Anneaux'),
(21, 'La vie des mineurs du nord de la France au XIXe siècle.', NULL, '9782200000021', '1885-01-01', 'Germinal'),
(22, 'Le portrait d''une femme prisonnière de ses rêves de province.', NULL, '9782200000022', '1857-01-01', 'Madame Bovary'),
(23, 'Un conte poétique sur l''enfance et l''essentiel.', NULL, '9782200000023', '1943-01-01', 'Le Petit Prince'),
(24, 'L''histoire d''Esmeralda et de Quasimodo dans le Paris médiéval.', NULL, '9782200000024', '1831-01-01', 'Notre-Dame de Paris'),
(25, 'Un ouvrage de référence sur l''amélioration du code existant.', NULL, '9782200000025', '1999-06-08', 'Refactoring')
GO
SET IDENTITY_INSERT [dbo].[books] OFF
GO

-- ============================================================
-- BOOK_AUTHOR
-- ============================================================
INSERT INTO [dbo].[book_author] ([book_id], [author_id]) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15), (16, 6), (17, 4), (18, 16),
(19, 17), (20, 18), (21, 19), (22, 20), (23, 21), (24, 1), (25, 22)
GO

-- ============================================================
-- BOOK_GENRE
-- ============================================================
INSERT INTO [dbo].[book_genre] ([book_id], [genre_id]) VALUES
(1, 1), (1, 7), (2, 1), (2, 2), (3, 2), (4, 4), (5, 2), (6, 3), (7, 1), (8, 1),
(9, 1), (10, 1), (11, 5), (12, 1), (13, 6), (14, 8), (15, 8), (16, 3), (17, 4),
(18, 2), (19, 2), (20, 3), (21, 7), (22, 5), (23, 1), (24, 1), (24, 7), (25, 8)
GO

-- ============================================================
-- BOOK_COPIES
-- 32 exemplaires : 18 AVAILABLE, 8 LOANED, 3 RESERVED, 2 IN_REPAIR, 1 LOST
-- ============================================================
SET IDENTITY_INSERT [dbo].[book_copies] ON
GO
INSERT INTO [dbo].[book_copies] ([id], [book_status], [book_condition], [serial_number], [book_id]) VALUES
(1, 'LOANED', 'GOOD', 'BH-COPY-0001', 1),
(2, 'RESERVED', 'NEW', 'BH-COPY-0002', 1),
(3, 'AVAILABLE', 'GOOD', 'BH-COPY-0003', 2),
(4, 'LOANED', 'GOOD', 'BH-COPY-0004', 3),
(5, 'AVAILABLE', 'WORN', 'BH-COPY-0005', 3),
(6, 'AVAILABLE', 'GOOD', 'BH-COPY-0006', 4),
(7, 'AVAILABLE', 'NEW', 'BH-COPY-0007', 5),
(8, 'LOANED', 'GOOD', 'BH-COPY-0008', 6),
(9, 'RESERVED', 'GOOD', 'BH-COPY-0009', 6),
(10, 'AVAILABLE', 'GOOD', 'BH-COPY-0010', 7),
(11, 'AVAILABLE', 'WORN', 'BH-COPY-0011', 8),
(12, 'LOANED', 'GOOD', 'BH-COPY-0012', 9),
(13, 'IN_REPAIR', 'DAMAGED', 'BH-COPY-0013', 9),
(14, 'AVAILABLE', 'GOOD', 'BH-COPY-0014', 10),
(15, 'AVAILABLE', 'WORN', 'BH-COPY-0015', 11),
(16, 'AVAILABLE', 'GOOD', 'BH-COPY-0016', 12),
(17, 'AVAILABLE', 'NEW', 'BH-COPY-0017', 13),
(18, 'LOANED', 'GOOD', 'BH-COPY-0018', 14),
(19, 'IN_REPAIR', 'DAMAGED', 'BH-COPY-0019', 14),
(20, 'AVAILABLE', 'NEW', 'BH-COPY-0020', 15),
(21, 'LOANED', 'GOOD', 'BH-COPY-0021', 16),
(22, 'LOST', 'WORN', 'BH-COPY-0022', 16),
(23, 'AVAILABLE', 'GOOD', 'BH-COPY-0023', 17),
(24, 'AVAILABLE', 'GOOD', 'BH-COPY-0024', 18),
(25, 'AVAILABLE', 'NEW', 'BH-COPY-0025', 19),
(26, 'LOANED', 'GOOD', 'BH-COPY-0026', 20),
(27, 'RESERVED', 'NEW', 'BH-COPY-0027', 20),
(28, 'AVAILABLE', 'GOOD', 'BH-COPY-0028', 21),
(29, 'AVAILABLE', 'WORN', 'BH-COPY-0029', 22),
(30, 'LOANED', 'GOOD', 'BH-COPY-0030', 23),
(31, 'AVAILABLE', 'NEW', 'BH-COPY-0031', 24),
(32, 'AVAILABLE', 'GOOD', 'BH-COPY-0032', 25)
GO
SET IDENTITY_INSERT [dbo].[book_copies] OFF
GO

-- ============================================================
-- USERS
-- Password : Pa$$w0rd1234
-- ============================================================
SET IDENTITY_INSERT [dbo].[users] ON
GO
INSERT INTO [dbo].[users] ([id], [email], [firstname], [lastname], [password], [phone], [role], [username]) VALUES
(1, 'admin@bookhub.com', 'Sophie', 'Marchand', '$2b$10$TH.6wt4fXVnyoN7ab0PkUuzxWYyoA4LpEVrhG.vnzXe2wZYJ.nMR2', '0600000001', 'ROLE_ADMIN', 'admin'),
(2, 'j.lefevre@bookhub.com', 'Julien', 'Lefèvre', '$2b$10$EjB2ZTG6MtFBfldmZGyV5.HzJ67HlHjVXmcAgyz/DkVLjT3CL2OZ.', '0600000002', 'ROLE_LIBRARIAN', 'jlefevre'),
(3, 'c.martin@bookhub.com', 'Camille', 'Martin', '$2b$10$awcf9cYYoiBun.WSC4ULF.39vWnTzLcsnqdSdF/FOC2YTF.TWgPpK', '0600000003', 'ROLE_LIBRARIAN', 'cmartin'),
(4, 'user@mail.com', 'Nathan', 'Dupuis', '$2b$10$tZD4XKtR2x41VrRc3SKJlOjoLYUOGtkCB.iqppyBYuLonGXz1Giv.', '0600000004', 'ROLE_USER', 'ndupuis'),
(5, 'lea.bernard@mail.fr', 'Léa', 'Bernard', '$2b$10$Ov0wsXvTzPdvM7V0KnrCJ.zHXB/ODSoy4WkZ2sgznGptKliKFGpCK', '0600000005', 'ROLE_USER', 'lbernard'),
(6, 'hugo.petit@mail.fr', 'Hugo', 'Petit', '$2b$10$9BZMmXzhMI8GThaTMf5jZeHQZY4WdBjIzMwiIAy7s/OCf83cU2MUC', '0600000006', 'ROLE_USER', 'hpetit'),
(7, 'chloe.robert@mail.fr', 'Chloé', 'Robert', '$2b$10$RWB2QX.HbdcvhkvQAUq7Eeqwry9/FT91m09MKa7DqOz2c0jEnqBRq', '0600000007', 'ROLE_USER', 'crobert'),
(8, 'louis.richard@mail.fr', 'Louis', 'Richard', '$2b$10$L4C1ll9FknmzkXEtqCDYVOXkkqYKe3Nm3CVnyGOwRMEfo8fMHTfAe', '0600000008', 'ROLE_USER', 'lrichard'),
(9, 'manon.durand@mail.fr', 'Manon', 'Durand', '$2b$10$D/uMWQ/wjlX4EtLXpGkL6eFqbhjPT1qa2JutlpMkJnFV/inxoyl16', '0600000009', 'ROLE_USER', 'mdurand'),
(10, 'enzo.moreau@mail.fr', 'Enzo', 'Moreau', '$2b$10$EsZd.GOCdsIu8h8hXvKLoeEi6.FpHmOLpOSI2BzJlq1sL7le0Fee6', '0600000010', 'ROLE_USER', 'emoreau'),
(11, 'jade.simon@mail.fr', 'Jade', 'Simon', '$2b$10$w8DETtYTgWGtXdkXj4m.jOSFdDbFJcf46idQPlCjGVGdLaR7SJAcu', '0600000011', 'ROLE_USER', 'jsimon'),
(12, 'gabriel.laurent@mail.fr', 'Gabriel', 'Laurent', '$2b$10$JVWzAffG/62zYPGcUKVZ9.OV104oSzuXWgDGVQV5FRhx45AFOcsP6', '0600000012', 'ROLE_USER', 'glaurent'),
(13, 'ines.michel@mail.fr', 'Inès', 'Michel', '$2b$10$kIsWjyuo/O5gyY1DLKc1ge5tWjsUwga8UlvGsvhyYOZDziGikrlAi', '0600000013', 'ROLE_USER', 'imichel'),
(14, 'raphael.garcia@mail.fr', 'Raphaël', 'Garcia', '$2b$10$QOogO60KE9YsE6pPN/TPoeByY5Eq15e41yySVJI.PM6vU/Z9HDjry', '0600000014', 'ROLE_USER', 'rgarcia'),
(15, 'camille.david@mail.fr', 'Camille', 'David', '$2b$10$mIRIJyPICq4lT/X/rZ57b.rJDwKyOzMOwOkQT8RGs.tXh45MVm/6C', '0600000015', 'ROLE_USER', 'cdavid')
GO
SET IDENTITY_INSERT [dbo].[users] OFF
GO

-- ============================================================
-- LOANS
-- Emprunts en cours (1-8, correspondent aux exemplaires LOANED)
-- puis historique d'emprunts rendus (9-18, exemplaires AVAILABLE)
-- ============================================================
SET IDENTITY_INSERT [dbo].[loans] ON
GO
INSERT INTO [dbo].[loans] ([id], [due_date], [loan_date], [return_date], [status], [book_copy_loaned_id], [loaner_id]) VALUES
(1, '2026-07-31', '2026-07-10', NULL, 'ACTIVE', 1, 4),
(2, '2026-07-19', '2026-07-05', NULL, 'OVERDUE', 4, 5),
(3, '2026-08-03', '2026-07-20', NULL, 'ACTIVE', 8, 6),
(4, '2026-06-29', '2026-06-15', NULL, 'OVERDUE', 12, 7),
(5, '2026-08-08', '2026-07-25', NULL, 'ACTIVE', 18, 8),
(6, '2026-07-29', '2026-07-15', NULL, 'OVERDUE', 21, 9),
(7, '2026-08-05', '2026-07-22', NULL, 'ACTIVE', 26, 10),
(8, '2026-08-11', '2026-07-28', NULL, 'ACTIVE', 30, 11),
(9, '2026-05-15', '2026-05-01', '2026-05-14', 'RETURNED', 3, 12),
(10, '2026-04-24', '2026-04-10', '2026-04-20', 'RETURNED', 5, 13),
(11, '2026-03-15', '2026-03-01', '2026-03-12', 'RETURNED', 6, 14),
(12, '2026-06-15', '2026-06-01', '2026-06-10', 'RETURNED', 7, 15),
(13, '2026-02-15', '2026-02-01', '2026-02-13', 'RETURNED', 10, 4),
(14, '2026-06-03', '2026-05-20', '2026-06-01', 'RETURNED', 11, 5),
(15, '2026-01-24', '2026-01-10', '2026-01-22', 'RETURNED', 14, 6),
(16, '2026-04-15', '2026-04-01', '2026-04-14', 'RETURNED', 15, 7),
(17, '2026-07-04', '2026-06-20', '2026-07-02', 'RETURNED', 16, 8),
(18, '2026-03-29', '2026-03-15', '2026-03-28', 'RETURNED', 17, 9)
GO
SET IDENTITY_INSERT [dbo].[loans] OFF
GO

-- ============================================================
-- RESERVATION
-- Réservations actives (1-3, correspondent aux exemplaires RESERVED)
-- puis historique de réservations terminées (4-6, sans exemplaire assigné)
-- ============================================================
SET IDENTITY_INSERT [dbo].[reservation] ON
GO
INSERT INTO [dbo].[reservation] ([reservation_id], [notified_at], [pickup_deadline], [reserves_date], [reservation_status], [book_id], [book_copy_id], [users_id]) VALUES
(1, '2026-07-29', '2026-08-03', '2026-07-27', 'READY_FOR_PICKUP', 1, 2, 12),
(2, '2026-07-28', '2026-08-01', '2026-07-25', 'READY_FOR_PICKUP', 6, 9, 13),
(3, NULL, NULL, '2026-07-30', 'PENDING', 20, 27, 14),
(4, '2026-06-05', '2026-06-08', '2026-06-01', 'EXPIRED', 3, NULL, 15),
(5, NULL, NULL, '2026-05-10', 'CANCELLED', 9, NULL, 4),
(6, '2026-07-03', '2026-07-06', '2026-07-01', 'FULFILLED', 14, NULL, 5)
GO
SET IDENTITY_INSERT [dbo].[reservation] OFF
GO

-- ============================================================
-- REVIEW
-- Avis laissés sur des livres déjà empruntés et rendus (loans 9-18)
-- ============================================================
SET IDENTITY_INSERT [dbo].[review] ON
GO
INSERT INTO [dbo].[review] ([id], [comment], [created_at], [moderated_at], [rating], [status], [updated_at], [book_id], [users_id]) VALUES
(1, 'Une aventure passionnante, digne de Jules Verne.', '2026-05-15', NULL, 4, 'ACTIVE', NULL, 2, 12),
(2, 'Un chef-d''oeuvre glaçant et toujours d''actualité.', '2026-04-21', NULL, 5, 'ACTIVE', NULL, 3, 13),
(3, 'Une enquête bien menée, du pur Agatha Christie.', '2026-03-13', NULL, 4, 'ACTIVE', NULL, 4, 14),
(4, 'La saga qui a changé la science-fiction.', '2026-06-11', NULL, 5, 'ACTIVE', NULL, 5, 15),
(5, 'Court mais percutant.', '2026-02-14', NULL, 4, 'ACTIVE', NULL, 7, 4),
(6, 'Étrange mais fascinant, un avis à nuancer.', '2026-06-02', '2026-06-03', 3, 'MODERATED', '2026-06-03', 8, 5),
(7, 'Toujours drôle après tout ce temps.', '2026-01-23', NULL, 4, 'ACTIVE', NULL, 10, 6),
(8, 'Une histoire d''amour bouleversante.', '2026-04-15', NULL, 4, 'ACTIVE', NULL, 11, 7),
(9, 'Dérangeant, pas pour tout le monde.', '2026-07-03', NULL, 2, 'ACTIVE', NULL, 12, 8),
(10, 'Un témoignage percutant sur le monde du travail.', '2026-03-29', NULL, 5, 'ACTIVE', NULL, 13, 9)
GO
SET IDENTITY_INSERT [dbo].[review] OFF
GO

-- ============================================================
-- REFRESH_TOKENS
-- Quelques jetons pour tester l'authentification
-- ============================================================
SET IDENTITY_INSERT [dbo].[refresh_tokens] ON
GO
INSERT INTO [dbo].[refresh_tokens] ([id], [expires_at], [revoked], [token_hash], [user_id]) VALUES
(1, '2026-08-30 10:00:00 +00:00', 0, 'refreshtoken-hash-admin-0000000001', 1),
(2, '2026-08-15 10:00:00 +00:00', 0, 'refreshtoken-hash-ndupuis-0000002', 4),
(3, '2026-07-20 10:00:00 +00:00', 1, 'refreshtoken-hash-jlefevre-0000003', 2)
GO
SET IDENTITY_INSERT [dbo].[refresh_tokens] OFF
GO
