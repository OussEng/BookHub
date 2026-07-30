import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookUpdateModal } from './book-update-modal';

describe('BookUpdateModal', () => {
  let component: BookUpdateModal;
  let fixture: ComponentFixture<BookUpdateModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookUpdateModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookUpdateModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
