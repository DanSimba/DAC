import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerMenu } from './manager-menu';

describe('ManagerMenu', () => {
  let component: ManagerMenu;
  let fixture: ComponentFixture<ManagerMenu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerMenu],
    }).compileComponents();

    fixture = TestBed.createComponent(ManagerMenu);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
