import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookCopyCreateModal } from './book-copy-create-modal';

describe('BookCopyCreateModal', () => {
  let component: BookCopyCreateModal;
  let fixture: ComponentFixture<BookCopyCreateModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookCopyCreateModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookCopyCreateModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
