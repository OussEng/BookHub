export interface LoansResponseModel {
    id: number;
    bookCopyId: number;
    userId: number;
    loanDate: string;
    dueDate: string;
    returnDate: string;
    status: string;
    bookTitle: string;
}