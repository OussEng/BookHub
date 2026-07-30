import {ReviewResponseModel} from "./response/review-response.model";

export interface ReviewPage {
    content: ReviewResponseModel[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
}