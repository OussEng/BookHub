package fr.eni.bookhub.bookcopy.service;



import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;

public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository) {
        this.bookCopyRepository = bookCopyRepository;
    }

    private boolean GoodCondition(BookCopy copy) {
        return copy.getCondition() == Condition.NEW
                || copy.getCondition() == Condition.GOOD;
    }

    public boolean canBeLoaned(BookCopy copy) {
        return copy.getBookStatus() == BookStatus.AVAILABLE && GoodCondition(copy);
    }

    public boolean canBeReserved(BookCopy copy) {
        return copy.getBookStatus() == BookStatus.LOANED && GoodCondition(copy);
    }

}
