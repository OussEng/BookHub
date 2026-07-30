import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Book} from '../../interfaces/book/book';
import {Observable} from 'rxjs';
import {Page} from '../../interfaces/page/page';
import {environment} from "../../../environments/environment";


@Injectable({providedIn: 'root'})
export class BookService {

    private readonly apiUrl = `${environment.apiUrl}/books`;


    constructor(private http: HttpClient) {

    }


    getBooks(
        page: number = 0,
        size: number = 20,
        search?: string,
        genreId?: number,
        sortBy: string = 'id',
        sortDir: string = 'desc'
    ): Observable<Page<Book>> {
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('sortBy', sortBy)
            .set('sortDir', sortDir);

        if (search && search.trim() !== '') {
            params = params.set('search', search.trim());
        }
        if (genreId !== undefined && genreId !== null) {
            params = params.set('genreId', genreId.toString());
        }

        return this.http.get<Page<Book>>(`${this.apiUrl}/all`, {params});
    }

    getBookById(id: number): Observable<Book> {
        return this.http.get<any>(`${this.apiUrl}/${id}`);
    }

    createBook(formData: FormData): Observable<Book> {
        return this.http.post<Book>(this.apiUrl, formData);
    }


    createBookWithImage(formData: FormData): Observable<Book> {
        return this.http.post<Book>(this.apiUrl, formData);
    }


    updateBookWithImage(id: number, formData: FormData): Observable<Book> {
        return this.http.put<Book>(`${this.apiUrl}/${id}`, formData);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
    }


}