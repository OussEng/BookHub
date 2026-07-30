import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ReviewPage} from '../../interfaces/review/review-page.model';
import {ReviewRequestModel} from "../../interfaces/review/request/review-request.model";
import {ReviewResponseModel} from "../../interfaces/review/response/review-response.model";
import {environment} from "../../../environments/environment";

@Injectable({providedIn: 'root'})
export class ReviewService {
    private readonly apiUrl = `${environment.apiUrl}/books`;

    constructor(private http: HttpClient) {
    }

    getReviewsByBook(bookId: number, page: number, size: number = 5): Observable<ReviewPage> {
        return this.http.get<ReviewPage>(`${this.apiUrl}/${bookId}/reviews?page=${page}&size=${size}`);
    }

    createReview(bookId: number, request: ReviewRequestModel): Observable<ReviewResponseModel> {
        return this.http.post<ReviewResponseModel>(`${this.apiUrl}/${bookId}/reviews`, request);
    }

    updateReview(bookId: number, reviewId: number, request: ReviewRequestModel): Observable<ReviewResponseModel> {
        return this.http.put<ReviewResponseModel>(`${this.apiUrl}/${bookId}/reviews/${reviewId}`, request);
    }

    deleteReview(bookId: number, reviewId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${bookId}/reviews/${reviewId}`);
    }
}