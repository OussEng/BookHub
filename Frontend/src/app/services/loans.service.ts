import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from 'rxjs';
import {LoansResponseModel} from "../interfaces/loans/loans-response-model";
import {Book} from "../interfaces/book/Book";
import {BookCopyService} from "./book-copy/book-copy.service";


@Injectable({
  providedIn: 'root',
})
export class LoansService {
  private readonly apiUrl = 'http://localhost:8080/api/loans';
  private bookCopyService: BookCopyService;


  constructor(private http: HttpClient, bookCopyService: BookCopyService) {
    this.bookCopyService = bookCopyService;
  }

  // API Requests

  getLoans(): Observable<LoansResponseModel[]> {
    return this.http.get<LoansResponseModel[]>(this.apiUrl, {withCredentials: true});
  }

  getMyLoans(): Observable<LoansResponseModel[]> {
    return this.http.get<LoansResponseModel[]>(`${this.apiUrl}/my`, {withCredentials: true});
  }

  newLoan(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/borrow`, {}, {withCredentials: true});
  }

  returnLoan(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/return`, {}, {withCredentials: true});
  }

  //Utils

  isBookAlreadyLoanedByUser(loans: LoansResponseModel[], book: Book): Observable<boolean> {
    let pendingLoansId:any[] = []
    let bookCopiesId:any[] = []

    return this.bookCopyService.getBookCopiesByBookId(book.id).pipe(map((result) => {
        result.content.forEach(val => bookCopiesId.push(val.id));

        for (let i = 0; i < loans.length; i++) {
          if(loans[i].status == 'ACTIVE') {
            pendingLoansId.push(loans[i].bookCopyId);
          }
        }

        return pendingLoansId.some((loans) => {
          return bookCopiesId.includes(loans)
        });
      }
    ));
  }
}
