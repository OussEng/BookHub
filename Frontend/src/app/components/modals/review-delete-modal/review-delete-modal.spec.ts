import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewDeleteModal } from './review-delete-modal';

describe('ReviewDeleteModal', () => {
  let component: ReviewDeleteModal;
  let fixture: ComponentFixture<ReviewDeleteModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewDeleteModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewDeleteModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
