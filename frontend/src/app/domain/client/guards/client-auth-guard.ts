import { CanActivateFn } from '@angular/router';
import { ClientService } from '../../../application/client/services/client-service';
import { inject } from '@angular/core';

export const clientAuthGuard: CanActivateFn = (route, state) => {
  const clientService = inject(ClientService);

  //"SOLUÇÃO" PROVISÓRIA TEM QUE ADICIONAR MAIS COISA AQ,
  // TALVEZ TROCAR POR UM USER SERVICE COM TIPO DE USER: CLIENTE OU MANAGER
  return clientService.logged();
};
