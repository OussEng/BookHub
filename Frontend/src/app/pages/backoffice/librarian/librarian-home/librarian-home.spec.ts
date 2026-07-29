import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibrarianHome } from './librarian-home';

describe('LibrarianHome', () => {
  let component: LibrarianHome;
  let fixture: ComponentFixture<LibrarianHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibrarianHome]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibrarianHome);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
