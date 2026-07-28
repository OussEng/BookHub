import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { FlashMessageService } from '../services/flash-message-service/flash-message-service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const flash = inject(FlashMessageService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const message = error.error?.error ?? error.error?.message;

      if (message) {
        flash.error(message);
      }

      return throwError(() => error);
    })
  );
};