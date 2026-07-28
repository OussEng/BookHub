import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibrarianBookCopies } from './librarian-book-copies';

describe('LibrarianBookCopies', () => {
  let component: LibrarianBookCopies;
  let fixture: ComponentFixture<LibrarianBookCopies>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibrarianBookCopies]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibrarianBookCopies);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
