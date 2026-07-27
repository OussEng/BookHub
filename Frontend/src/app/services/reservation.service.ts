import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservationRequest } from '../interfaces/reservation/request/reservation-request.model';
import { ReservationResponse } from '../interfaces/reservation/response/reservation-response.model';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  reserver(request: ReservationRequest): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(this.apiUrl, request);
  }

  mesReservations(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(`${this.apiUrl}/me`);
  }

  annuler(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
