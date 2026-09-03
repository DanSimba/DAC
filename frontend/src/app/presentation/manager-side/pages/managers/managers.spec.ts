import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Managers } from './managers';

describe('Managers', () => {
  let component: Managers;
  let fixture: ComponentFixture<Managers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Managers],
    }).compileComponents();

    fixture = TestBed.createComponent(Managers);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
