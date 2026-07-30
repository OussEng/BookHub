import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibrarianReviews } from './librarian-reviews';

describe('LibrarianReviews', () => {
  let component: LibrarianReviews;
  let fixture: ComponentFixture<LibrarianReviews>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibrarianReviews]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibrarianReviews);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
