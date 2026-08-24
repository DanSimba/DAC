import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Transference } from './transference';

describe('Transference', () => {
  let component: Transference;
  let fixture: ComponentFixture<Transference>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Transference],
    }).compileComponents();

    fixture = TestBed.createComponent(Transference);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
