import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepSac } from './dep-sac';

describe('DepSac', () => {
  let component: DepSac;
  let fixture: ComponentFixture<DepSac>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepSac],
    }).compileComponents();

    fixture = TestBed.createComponent(DepSac);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
