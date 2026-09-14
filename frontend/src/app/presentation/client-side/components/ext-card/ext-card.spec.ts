import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExtCard } from './ext-card';

describe('ExtCard', () => {
  let component: ExtCard;
  let fixture: ComponentFixture<ExtCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExtCard],
    }).compileComponents();

    fixture = TestBed.createComponent(ExtCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
