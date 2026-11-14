import { CanActivateFn, Router } from '@angular/router';
import { ClientService } from '../../../application/client/services/client-service';
import { inject } from '@angular/core';
import { UserType } from '../../../enumeration/user-type';
import { AuthHttpService } from '../../../infraestructure/http/auth.http.service';

export function roleGuard(perfilEsperado: UserType): CanActivateFn {
  return () => {
    const authService = inject(AuthHttpService);
    const router = inject(Router);

    if (!authService.islogged()) {
      return router.createUrlTree(['/login']);
    }

    const perfilAtual = authService.getTipo();

    
    if (perfilAtual === perfilEsperado) {
      return true;
    }

    switch (perfilAtual) {
      case UserType.CLIENT: return router.createUrlTree(['/client']);
      case UserType.MANAGER: return router.createUrlTree(['/manager']); 
      default: return router.createUrlTree(['/login']);
    }
  }
}

export const clientAuthGuard: CanActivateFn = roleGuard(UserType.CLIENT);
export const managerAuthGuard: CanActivateFn = roleGuard(UserType.MANAGER);