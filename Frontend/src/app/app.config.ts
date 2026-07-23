import { ApplicationConfig, inject, provideAppInitializer } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withXsrfConfiguration } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';
import { credentialsInterceptor } from './interceptors/credentials.interceptor';
import { AuthService } from './services/auth.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor, credentialsInterceptor]),
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN'
      })
    ),
    provideAppInitializer(() => {
      const authService = inject(AuthService);
      return authService.refresh().pipe(catchError(() => of(null)));
    })
  ]
};