import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { Observable } from 'rxjs';
import {LoansResponseModel} from "../interfaces/loans/loans-response-model";


@Injectable({
  providedIn: 'root',
})
export class LoansService {
  private readonly apiUrl = 'http://localhost:8080/api/loans';


  constructor(private http: HttpClient) { }

  getLoans(): Observable<LoansResponseModel> {
    return this.http.get<LoansResponseModel>(this.apiUrl, {withCredentials: true});
  }

  getMyLoans(): Observable<LoansResponseModel> {
    return this.http.get<LoansResponseModel>(`${this.apiUrl}/my`, {withCredentials: true});
  }

  newLoan(id: number): void {
    this.http.post<void>(`${this.apiUrl}/${id}/borrow`, {}, {withCredentials: true}).subscribe();
  }

  returnLoan(id: number): void {
    this.http.put<void>(`${this.apiUrl}/${id}/return`, {}, {withCredentials: true}).subscribe();
  }
}
