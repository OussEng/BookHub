import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookCreateModal } from './book-create-modal';

describe('BookCreateModal', () => {
  let component: BookCreateModal;
  let fixture: ComponentFixture<BookCreateModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookCreateModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookCreateModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
