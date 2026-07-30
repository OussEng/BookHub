import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Author } from "../../interfaces/author/author";




@Injectable({ providedIn: 'root' })
export class AuthorService {



private readonly apiUrl = 'http://localhost:8080/api/author';
  

  constructor(private http: HttpClient) {

  }


    getAuthors(): Observable<Author[]> {
    return this.http.get<Author[]>(this.apiUrl + '/all');
  }
  
    
}