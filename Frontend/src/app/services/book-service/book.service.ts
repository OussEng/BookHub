import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Book } from '../../interfaces/book/Book';
import { Observable } from 'rxjs';



@Injectable({ providedIn: 'root' })
export class BookService {
 
  private readonly apiUrl = 'http://localhost:8080/api/books';
  

  constructor(private http: HttpClient) {

  }


    getBooks(): Observable<Book[]> {

    return this.http.get<Book[]>(this.apiUrl + '/all');

  }

  getBookById(id: number): Observable<Book> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}