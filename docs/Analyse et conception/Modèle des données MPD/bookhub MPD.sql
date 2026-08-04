CREATE TABLE book 
    (
     id NUMERIC (19) NOT NULL , 
     title VARCHAR (255) NOT NULL , 
     author VARCHAR (255) NOT NULL , 
     isbn VARCHAR (20) NOT NULL , 
     img VARCHAR (500) NOT NULL , 
     publish_date DATE NOT NULL , 
     description VARCHAR (2000) 
    )
GO

ALTER TABLE book ADD CONSTRAINT book_PK PRIMARY KEY CLUSTERED (id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO
ALTER TABLE book ADD CONSTRAINT book_isbn_UN UNIQUE NONCLUSTERED (isbn)
GO

CREATE TABLE book_copy 
    (
     id NUMERIC (19) NOT NULL , 
     book_id NUMERIC (19) NOT NULL , 
     serial_number VARCHAR (100) NOT NULL , 
     book_status VARCHAR (20) NOT NULL , 
     condition VARCHAR (20) NOT NULL 
    )
GO

ALTER TABLE book_copy ADD CONSTRAINT book_copy_PK PRIMARY KEY CLUSTERED (id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO
ALTER TABLE book_copy ADD CONSTRAINT book_copy_serial_number_UN UNIQUE NONCLUSTERED (serial_number)
GO
ALTER TABLE book_copy ADD CONSTRAINT book_copy_status_CK CHECK ( book_status IN (
    'AVAILABLE','LOANED','LOST','IN_REPAIR' ) )
GO
ALTER TABLE book_copy ADD CONSTRAINT book_copy_condition_CK CHECK ( condition IN (
    'NEW','GOOD','WORN','DAMAGED' ) )
GO
CREATE NONCLUSTERED INDEX book_copy_book_id_IX ON book_copy (book_id)
GO

CREATE TABLE genre 
    (
     id NUMERIC (19) NOT NULL , 
     label VARCHAR (100) NOT NULL 
    )
GO

ALTER TABLE genre ADD CONSTRAINT genre_PK PRIMARY KEY CLUSTERED (id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO
ALTER TABLE genre ADD CONSTRAINT genre_label_UN UNIQUE NONCLUSTERED (label)
GO

CREATE TABLE genre_book 
    (
     genre_id NUMERIC (19) NOT NULL , 
     book_id NUMERIC (19) NOT NULL 
    )
GO

ALTER TABLE genre_book ADD CONSTRAINT genre_book_PK PRIMARY KEY CLUSTERED (genre_id, book_id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO

CREATE TABLE loan 
    (
     id NUMERIC (19) NOT NULL , 
     users_id NUMERIC (19) NOT NULL , 
     book_copy_id NUMERIC (19) NOT NULL , 
     loan_date DATE NOT NULL , 
     return_date DATE , 
     due_date DATE NOT NULL , 
     loan_status VARCHAR (20) NOT NULL
    )
GO

ALTER TABLE loan ADD CONSTRAINT loan_PK PRIMARY KEY CLUSTERED (id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO
ALTER TABLE loan ADD CONSTRAINT loan_loan_status_CK CHECK ( loan_status IN (
    'ACTIVE','RETURNED','OVERDUE','EXPIRED') )
GO

CREATE NONCLUSTERED INDEX loan_users_id_IX ON loan (users_id)
GO
CREATE NONCLUSTERED INDEX loan_book_copy_id_IX ON loan (book_copy_id)
GO

CREATE TABLE reservation 
    (
     id NUMERIC (19) NOT NULL , 
     users_id NUMERIC (19) NOT NULL , 
     book_id NUMERIC (19) NOT NULL , 
     reserve_date DATE NOT NULL , 
     reservation_status VARCHAR (20) NOT NULL 
    )
GO

ALTER TABLE reservation ADD CONSTRAINT reservation_PK PRIMARY KEY CLUSTERED (id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO
ALTER TABLE reservation ADD CONSTRAINT reservation_reservation_status_CK CHECK ( reservation_status IN (
    'PENDING','FULFILLED','CANCELLED','EXPIRED' ) )
GO
CREATE NONCLUSTERED INDEX reservation_users_id_IX ON reservation (users_id)
GO
CREATE NONCLUSTERED INDEX reservation_book_id_IX ON reservation (book_id)
GO

CREATE TABLE review 
    (
     id NUMERIC (19) NOT NULL , 
     users_id NUMERIC (19) NOT NULL , 
     book_id NUMERIC (19) NOT NULL , 
     rating NUMERIC (1) NOT NULL , 
     comment VARCHAR (2000) 
    )
GO

ALTER TABLE review ADD CONSTRAINT review_PK PRIMARY KEY CLUSTERED (id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO
ALTER TABLE review ADD CONSTRAINT review_rating_CK CHECK ( rating BETWEEN 1 AND 5 )
GO
ALTER TABLE review ADD CONSTRAINT review_user_book_UN UNIQUE NONCLUSTERED (users_id, book_id)
GO
CREATE NONCLUSTERED INDEX review_users_id_IX ON review (users_id)
GO
CREATE NONCLUSTERED INDEX review_book_id_IX ON review (book_id)
GO

CREATE TABLE users 
    (
     id NUMERIC (19) NOT NULL , 
     firstname VARCHAR (100) NOT NULL , 
     lastname VARCHAR (100) NOT NULL , 
     username VARCHAR (50) NOT NULL , 
     phone VARCHAR (30) NOT NULL , 
     email VARCHAR (150) NOT NULL , 
     password VARCHAR (255) NOT NULL , 
     role VARCHAR (20) NOT NULL 
    )
GO

ALTER TABLE users ADD CONSTRAINT users_PK PRIMARY KEY CLUSTERED (id)
     WITH ( ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON )
GO
ALTER TABLE users ADD CONSTRAINT users_username_UN UNIQUE NONCLUSTERED (username)
GO
ALTER TABLE users ADD CONSTRAINT users_email_UN UNIQUE NONCLUSTERED (email)
GO
ALTER TABLE users ADD CONSTRAINT users_role_CK CHECK ( role IN (
    'ROLE_USER','ROLE_ADMIN','ROLE_LIBRARIAN' ) )
GO

-- Foreign keys

ALTER TABLE book_copy 
    ADD CONSTRAINT book_copy_book_FK FOREIGN KEY (book_id) REFERENCES book (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE genre_book 
    ADD CONSTRAINT genre_book_book_FK FOREIGN KEY (book_id) REFERENCES book (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE genre_book 
    ADD CONSTRAINT genre_book_genre_FK FOREIGN KEY (genre_id) REFERENCES genre (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE loan 
    ADD CONSTRAINT loan_book_copy_FK FOREIGN KEY (book_copy_id) REFERENCES book_copy (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE loan 
    ADD CONSTRAINT loan_users_FK FOREIGN KEY (users_id) REFERENCES users (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE reservation 
    ADD CONSTRAINT reservation_book_FK FOREIGN KEY (book_id) REFERENCES book (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE reservation 
    ADD CONSTRAINT reservation_users_FK FOREIGN KEY (users_id) REFERENCES users (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE review 
    ADD CONSTRAINT review_book_FK FOREIGN KEY (book_id) REFERENCES book (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO

ALTER TABLE review 
    ADD CONSTRAINT review_users_FK FOREIGN KEY (users_id) REFERENCES users (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION 
GO
