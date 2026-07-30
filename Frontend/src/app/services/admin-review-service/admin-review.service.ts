import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from "../../../environments/environment";
import {AdminReviewResponse} from "../../interfaces/review/response/admin-review-response";


export interface PageResponse<T> {
  content: T[];
  number: number;
  totalPages: number;
  totalElements: number;
}

@Injectable({ providedIn: 'root' })
export class AdminReviewService {
  private readonly apiUrl = `${environment.apiUrl}/admin/reviews`;

  constructor(private http: HttpClient) {}

  getReviews(page: number, size: number, search: string): Observable<PageResponse<AdminReviewResponse>> {
    let params = new HttpParams()
        .set('page', page)
        .set('size', size);

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<PageResponse<AdminReviewResponse>>(this.apiUrl, { params, withCredentials: true });
  }

  moderateReview(reviewId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${reviewId}/moderate`, {}, { withCredentials: true });
  }
}