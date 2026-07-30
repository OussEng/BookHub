import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Book} from '../interfaces/book/book';
import {environment} from "../../environments/environment";

@Injectable({providedIn: 'root'})
export class BookService {
    private readonly apiUrl = `${environment.apiUrl}/books`;

    constructor(private http: HttpClient) {
    }

    getAllBooks(): Observable<Book[]> {
        return this.http.get<Book[]>(this.apiUrl);
    }

    getBookById(id: number): Observable<Book> {
        return this.http.get<Book>(`${this.apiUrl}/${id}`);
    }
}