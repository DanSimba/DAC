import { TestBed } from '@angular/core/testing';

import { ClientHttpService } from './client.http.service';

describe('ClientHttpService', () => {
  let service: ClientHttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientHttpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
