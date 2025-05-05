// src/app/core/interceptors/error-interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';
import { TokenService } from '../services/token.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const tokenService = inject(TokenService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Неавторизованный доступ - выход из системы
        tokenService.signOut();
        router.navigate(['/auth/login']);
      }

      // Обработка других ошибок
      const errorMessage = error.error?.message || 'Произошла ошибка при выполнении запроса';
      console.error('HTTP Error:', errorMessage);

      return throwError(() => new Error(errorMessage));
    })
  );
};
