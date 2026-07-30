import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservationResponse } from '../interfaces/reservation/response/reservation-response.model';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly apiUrl = 'http://localhost:8080/api/reservations';
  // Le retrait passe par le module Emprunt : deuxième base d'URL assumée.
  private readonly loansUrl = 'http://localhost:8080/api/loans';

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

   // « Prendre » : le retrait passe par l'endpoint d'emprunt.
  // createLoan appelle fulfillIfReady, qui reconnaît la réservation
  // prête et la clôt en FULFILLED. Il n'y a pas d'endpoint « Prendre ».
  prendre(bookId: number): Observable<void> {
    return this.http.post<void>(`${this.loansUrl}/${bookId}/borrow`, null);
  }

}
