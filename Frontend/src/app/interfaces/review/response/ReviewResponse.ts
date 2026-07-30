export interface ReviewResponse {
    id: number;
    userId: number;
    username: string;
    bookId: number;
    rating: number;
    comment: string | null;
    moderated: boolean;
    createdAt: string;
    updatedAt: string;
}