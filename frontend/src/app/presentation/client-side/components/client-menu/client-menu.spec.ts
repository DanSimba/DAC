import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientMenu } from './client-menu';

describe('ClientMenu', () => {
  let component: ClientMenu;
  let fixture: ComponentFixture<ClientMenu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientMenu],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientMenu);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
