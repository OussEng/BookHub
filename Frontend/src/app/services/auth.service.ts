import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../interfaces/auth/request/login-request.model';
import { LoginResponse } from '../interfaces/auth/request/login-response.model';
import { RegisterRequest } from '../interfaces/auth/response/register-request.model';
import { RegisterResponse } from '../interfaces/auth/response/register-response.model';
import { CurrentUser } from '../interfaces/current user/current-user';


@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private accessToken: string | null = null;
  private currentUser: CurrentUser | null = null;

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request)
      .pipe(tap(response => this.setSession(response)));
  }

  register(request: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, request);
  }

  refresh(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/refresh`, {})
      .pipe(tap(response => this.setSession(response)));
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe();
    this.clearToken();
  }

  private setSession(response: LoginResponse): void {
  this.accessToken = response.accessToken;
  this.currentUser = {
    id: response.id,
    username: response.username,
    firstname: response.firstname,
    lastname: response.lastname,
    phone: response.phone,
    email: response.email,
    role: response.role
  };
}

  setToken(token: string): void {
    this.accessToken = token;
  }

  getToken(): string | null {
    return this.accessToken;
  }

  getCurrentUser(): CurrentUser | null {
    return this.currentUser;
  }

  clearToken(): void {
    this.accessToken = null;
    this.currentUser = null;
  }

  isAuthenticated(): boolean {
    return this.accessToken !== null;
  }
}