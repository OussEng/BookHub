import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Genre} from "../../interfaces/genre/genre";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";


@Injectable({providedIn: 'root'})
export class GenreService {


    private readonly apiUrl = `${environment.apiUrl}/genre`;


    constructor(private http: HttpClient) {

    }


    getGenres(): Observable<Genre[]> {
        return this.http.get<Genre[]>(this.apiUrl + '/all');
    }

}