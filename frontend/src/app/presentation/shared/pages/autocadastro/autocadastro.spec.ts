import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Autocadastro } from './autocadastro';

describe('Autocadastro', () => {
  let component: Autocadastro;
  let fixture: ComponentFixture<Autocadastro>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Autocadastro]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Autocadastro);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
