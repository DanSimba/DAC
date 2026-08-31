import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerNavBar } from './manager-nav-bar';

describe('ManagerNavBar', () => {
  let component: ManagerNavBar;
  let fixture: ComponentFixture<ManagerNavBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerNavBar],
    }).compileComponents();

    fixture = TestBed.createComponent(ManagerNavBar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
