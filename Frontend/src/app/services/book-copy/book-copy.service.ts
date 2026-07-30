import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookCopy } from '../../interfaces/book-copy/book-copy';
import { CreateBookCopyDto } from '../../interfaces/book-copy/book-copy-create';
import { BookCondition } from '../../interfaces/book-copy/book-copy-condition';
import { Page } from '../../interfaces/page/page';



@Injectable({ providedIn: 'root' })
export class BookCopyService {
 
  private readonly apiUrl = 'http://localhost:8080/api/book-copies';

  constructor(private http: HttpClient) {}

  getBookCopiesByBookId(
    bookId: number,
    page: number = 0,
    size: number = 10,
    serialNumber?: string,
    condition?: BookCondition
  ): Observable<Page<BookCopy>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (serialNumber && serialNumber.trim() !== '') {
      params = params.set('serialNumber', serialNumber.trim());
    }
    if (condition) {
      params = params.set('condition', condition);
    }

    return this.http.get<Page<BookCopy>>(`${this.apiUrl}/book/${bookId}`, { params });
  }

  getBookCopyById(id: number): Observable<BookCopy> {
    return this.http.get<BookCopy>(`${this.apiUrl}/${id}`);
  }

  
  createBookCopy(copyData: CreateBookCopyDto): Observable<BookCopy> {
    return this.http.post<BookCopy>(`${this.apiUrl}/create`, copyData);
  }
}