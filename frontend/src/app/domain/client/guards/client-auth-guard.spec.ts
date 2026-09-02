import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { clientAuthGuard } from './client-auth-guard';

describe('clientAuthGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => clientAuthGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
