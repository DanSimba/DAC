import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientSide } from './client-side';

describe('ClientSide', () => {
  let component: ClientSide;
  let fixture: ComponentFixture<ClientSide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientSide],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientSide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
