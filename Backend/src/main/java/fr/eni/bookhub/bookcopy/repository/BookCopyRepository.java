package fr.eni.bookhub.bookcopy.repository;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
}
