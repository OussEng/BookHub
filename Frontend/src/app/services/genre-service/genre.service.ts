import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Genre } from "../../interfaces/genre/genre";
import { Observable } from "rxjs";




@Injectable({ providedIn: 'root' })
export class GenreService {
  

    private readonly apiUrl = 'http://localhost:8080/api/genre';
  

  constructor(private http: HttpClient) {

  }


    getGenres(): Observable<Genre[]> {
    return this.http.get<Genre[]>(this.apiUrl + '/all');
  }
    
}