import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookDeleteModal } from './book-delete-modal';

describe('BookDeleteModal', () => {
  let component: BookDeleteModal;
  let fixture: ComponentFixture<BookDeleteModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookDeleteModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookDeleteModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
