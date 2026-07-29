import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibrarianBookCatalogue } from './librarian-book-catalogue';

describe('LibrarianBookCatalogue', () => {
  let component: LibrarianBookCatalogue;
  let fixture: ComponentFixture<LibrarianBookCatalogue>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibrarianBookCatalogue]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibrarianBookCatalogue);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
