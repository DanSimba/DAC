import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerHomePage } from './manager-home-page';

describe('ManagerHomePage', () => {
  let component: ManagerHomePage;
  let fixture: ComponentFixture<ManagerHomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerHomePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerHomePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
