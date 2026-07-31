import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ReservationResponse} from '../interfaces/reservation/response/reservation-response.model';
import {environment} from "../../environments/environment";
import {BookActionResponse} from "../interfaces/reservation/response/book-action.model";

@Injectable({providedIn: 'root'})
export class ReservationService {
    private readonly apiUrl = `${environment.apiUrl}/reservations`;

    constructor(private http: HttpClient) {
    }

    reserver(bookId: number): Observable<ReservationResponse> {
        return this.http.post<ReservationResponse>(`${this.apiUrl}/${bookId}`, null);
    }

    mesReservations(): Observable<ReservationResponse[]> {
        return this.http.get<ReservationResponse[]>(`${this.apiUrl}/my`);

    }

    annuler(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    getAvailableAction(bookId: number): Observable<BookActionResponse> {
        return this.http.get<BookActionResponse>(`${this.apiUrl}/book/${bookId}/action`, { withCredentials: true });
    }

    createReservation(bookId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${bookId}`, {}, { withCredentials: true });
    }

    cancelReservation(reservationId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${reservationId}`, { withCredentials: true });
    }

    // « Prendre » : le retrait passe par l'endpoint d'emprunt.
    // createLoan appelle fulfillIfReady, qui reconnaît la réservation
    // prête et la clôt en FULFILLED. Il n'y a pas d'endpoint « Prendre ».
    prendre(bookId: number): Observable<void> {
        return this.http.post<void>(`${environment.apiUrl}/loans/${bookId}/borrow`, null);
    }
}
