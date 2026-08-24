import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerSidePage } from './manager-side-page';

describe('ManagerSidePage', () => {
  let component: ManagerSidePage;
  let fixture: ComponentFixture<ManagerSidePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerSidePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerSidePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
