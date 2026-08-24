import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeWindow } from './home-window';

describe('HomeWindow', () => {
  let component: HomeWindow;
  let fixture: ComponentFixture<HomeWindow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeWindow],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeWindow);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
