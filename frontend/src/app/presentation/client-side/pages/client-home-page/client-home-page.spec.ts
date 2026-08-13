import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientHomePage } from './client-home-page';

describe('ClientHomePage', () => {
  let component: ClientHomePage;
  let fixture: ComponentFixture<ClientHomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientHomePage],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientHomePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
