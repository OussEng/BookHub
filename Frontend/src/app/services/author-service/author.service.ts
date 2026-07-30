import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Author} from "../../interfaces/author/author";
import {environment} from "../../../environments/environment";


@Injectable({providedIn: 'root'})
export class AuthorService {


    private readonly apiUrl = `${environment.apiUrl}/author`;


    constructor(private http: HttpClient) {

    }


    getAuthors(): Observable<Author[]> {
        return this.http.get<Author[]>(this.apiUrl + '/all');
    }


}