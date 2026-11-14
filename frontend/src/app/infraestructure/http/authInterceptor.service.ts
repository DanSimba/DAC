import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthHttpService } from './auth.http.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthHttpService);
    const token = authService.getToken();
    const isRotaPublica = req.url.includes('/auth/login') || req.url.includes('/autocadastro');

    let requestClonada = req;

    if (token && !isRotaPublica) {
        requestClonada = req.clone({
            setHeaders: {
                'x-access-token' : token
            }
        });
    }

    return next(requestClonada).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401 && !isRotaPublica) {
                authService.limparSessaoLocal();
            }
            return throwError(() => error);
        })
    );
};