// src/app/core/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';

  constructor(private http: HttpClient) { }

  login(email: string, password: string): Observable<any> {
    console.log('Отправка запроса:', { email, password: '***' });

    return this.http.post<any>(`${this.apiUrl}/signin`, {
      username: email,
      password
    }).pipe(
      tap(response => console.log('Ответ от сервера:', response)),
      map(response => {
        if (response && response.data) {
          return response.data;
        }
        return response;
      }),
      catchError(error => {
        console.error('Ошибка аутентификации:', error);
        return throwError(() => new Error(error.error?.message || 'Ошибка аутентификации'));
      })
    );
  }

  register(username: string, email: string, password: string, firstName?: string, lastName?: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/signup`, {
      username,
      email,
      password,
      firstName,
      lastName
    }).pipe(
      map(response => {
        if (response && response.data) {
          return response.data;
        }
        return response;
      }),
      catchError(error => {
        console.error('Ошибка регистрации:', error);
        return throwError(() => new Error(error.error?.message || 'Ошибка при регистрации'));
      })
    );
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('auth-token');
    return !!token;
  }
}
