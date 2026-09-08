import { TestBed } from '@angular/core/testing';

import { AccountHttpService } from './account.http.service';

describe('AccountHttpService', () => {
  let service: AccountHttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountHttpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
