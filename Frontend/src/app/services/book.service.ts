import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';
import { Book } from '../interfaces/book/book';

@Injectable({ providedIn: 'root' })
export class BookService {
    private readonly apiUrl = 'http://localhost:8080/api/books';

    constructor(private http: HttpClient) {}

    getAllBooks(): Observable<Book[]> {
        return this.http.get<Book[]>(this.apiUrl);
    }

    getBookById(id: number): Observable<Book> {
        return this.http.get<Book>(`${this.apiUrl}/${id}`);
    }
}