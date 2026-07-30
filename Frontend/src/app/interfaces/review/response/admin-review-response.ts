export interface AdminReviewResponse {
    id: number;
    username: string;
    bookId: number;
    bookTitle: string;
    bookAuthors: string[];
    rating: number;
    comment: string | null;
    moderated: boolean;
    createdAt: string;
    updatedAt: string | null;
    moderatedAt: string | null;
}