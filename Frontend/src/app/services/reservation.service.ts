import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservationResponse } from '../interfaces/reservation/response/reservation-response.model';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  reserver(bookId: number): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(`${this.apiUrl}/${bookId}`, null);
  }

  mesReservations(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(`${this.apiUrl}/my`);

  }

  annuler(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
