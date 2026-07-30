import {ReviewResponse} from "./response/ReviewResponse";

export interface ReviewPage {
    content: ReviewResponse[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
}