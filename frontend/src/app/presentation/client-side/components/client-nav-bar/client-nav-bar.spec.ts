import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientNavBar } from './client-nav-bar';

describe('ClientNavBar', () => {
  let component: ClientNavBar;
  let fixture: ComponentFixture<ClientNavBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientNavBar],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientNavBar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
