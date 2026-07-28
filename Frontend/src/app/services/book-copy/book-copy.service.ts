import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookCopy } from '../../interfaces/book-copy/book-copy';


@Injectable({ providedIn: 'root' })
export class BookCopyService {
 
  private readonly apiUrl = 'http://localhost:8080/api/book-copies';

  constructor(private http: HttpClient) {}

  getBookCopiesByBookId(bookId: number): Observable<BookCopy[]> {
    return this.http.get<BookCopy[]>(`${this.apiUrl}/book/${bookId}`);
  }

  getBookCopyById(id: number): Observable<BookCopy> {
    return this.http.get<BookCopy>(`${this.apiUrl}/${id}`);
  }

  createBookCopy(copyData: any): Observable<BookCopy> {
    return this.http.post<BookCopy>(this.apiUrl, copyData);
  }

}