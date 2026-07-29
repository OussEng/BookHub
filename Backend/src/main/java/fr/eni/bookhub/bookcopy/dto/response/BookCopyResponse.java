package fr.eni.bookhub.bookcopy.dto.response;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookCopyResponse {

    private final Long id;
    private final String serialNumber;
    private final String bookTitle;
    private final BookStatus bookStatus;
    private final Condition condition;
    private final boolean canBeLoaned;
    private final boolean canBeReserved;

    public static BookCopyResponse fromBookCopyEntity(BookCopy copy) {
        boolean goodCondition = copy.getCondition() == Condition.NEW
                || copy.getCondition() == Condition.GOOD;

        return new BookCopyResponse(
                copy.getId(),
                copy.getSerialNumber(),
                copy.getBook().getTitle(),
                copy.getBookStatus(),
                copy.getCondition(),
                copy.getBookStatus() == BookStatus.AVAILABLE && goodCondition,
                copy.getBookStatus() == BookStatus.LOANED && goodCondition
        );
    }
}
