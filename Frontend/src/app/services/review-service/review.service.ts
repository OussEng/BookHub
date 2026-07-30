import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReviewPage } from '../../interfaces/review/review-page.model';
import {ReviewRequest} from "../../interfaces/review/request/ReviewRequest";
import {ReviewResponse} from "../../interfaces/review/response/ReviewResponse";

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly apiUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) {}

  getReviewsByBook(bookId: number, page: number, size: number = 5): Observable<ReviewPage> {
    return this.http.get<ReviewPage>(`${this.apiUrl}/${bookId}/reviews?page=${page}&size=${size}`);
  }

  createReview(bookId: number, request: ReviewRequest): Observable<ReviewResponse> {
    return this.http.post<ReviewResponse>(`${this.apiUrl}/${bookId}/reviews`, request);
  }

  updateReview(bookId: number, reviewId: number, request: ReviewRequest): Observable<ReviewResponse> {
    return this.http.put<ReviewResponse>(`${this.apiUrl}/${bookId}/reviews/${reviewId}`, request);
  }

  deleteReview(bookId: number, reviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${bookId}/reviews/${reviewId}`);
  }
}