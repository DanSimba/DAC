import { TestBed } from '@angular/core/testing';

import { Viacep } from '../../../services/viacep';

describe('Viacep', () => {
  let service: Viacep;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Viacep);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
