import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const refreshInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError(error => {
      if (error.status === 401 && !req.url.includes('/auth/refresh') && !req.url.includes('/auth/login')) {
        return authService.refresh().pipe(
          switchMap(() => next(req)),
          catchError(refreshError => {
            authService.clearToken();
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
};