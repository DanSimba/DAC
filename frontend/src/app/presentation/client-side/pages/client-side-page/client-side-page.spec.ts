import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientSidePage } from './client-side-page';

describe('ClientSidePage', () => {
  let component: ClientSidePage;
  let fixture: ComponentFixture<ClientSidePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientSidePage],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientSidePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
