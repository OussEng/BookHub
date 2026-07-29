package fr.eni.bookhub.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.eni.bookhub.book.entity.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}

