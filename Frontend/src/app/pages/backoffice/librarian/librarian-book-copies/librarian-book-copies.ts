import { Component, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookService } from '../../../../services/book-service/book.service';
import { BookCopyService } from '../../../../services/book-copy/book-copy.service';
import { BookCopy } from '../../../../interfaces/book-copy/book-copy';
import { BookCondition } from '../../../../interfaces/book-copy/book-copy-condition';
import { BookStatus } from '../../../../interfaces/book-copy/book-copy-status';

@Component({
  selector: 'app-librarian-book-copies',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './librarian-book-copies.html',
  styleUrl: './librarian-book-copies.css',
})
export class LibrarianBookCopies {
  private bookService = inject(BookService);
  private bookCopyService = inject(BookCopyService); 

  id = input.required<string>();

  book: any = null;
  protected copies = signal<BookCopy[]>([]);
  protected isLoading = signal<boolean>(true);
  errorMessage = '';

  ngOnInit() {
    const bookId = Number(this.id());

    /*
    this.bookService.getBookById(bookId).subscribe({
      next: (data) => {
        this.book = data;
      },
      error: (err) => {
        this.errorMessage = "Impossible de charger les détails du livre.";
        console.error(err);
      }
    });

    this.bookCopyService.getBookCopiesByBookId(bookId).subscribe({
      next: (data) => {
        this.copies.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage = "Impossible de charger les exemplaires.";
        this.isLoading.set(false);
        console.error(err);
      }
    });
    */

    setTimeout(() => {
      this.book = {
        id: bookId,
        title: 'Les Misérables',
        author: ['Victor Hugo'],
        genres: ['Roman'],
        isbn: '978-2070409181'
      };

      this.copies.set([
        {
          id: 1,
          serialNumber: 'SN-987654-01',
          condition: BookCondition.GOOD,
          bookId: bookId,
          isAvailable: true,
          bookStatus: BookStatus.AVAILABLE
        },
        {
          id: 2,
          serialNumber: 'SN-987654-02',
          condition: BookCondition.GOOD,
          bookId: bookId,
          isAvailable: false,
          bookStatus: BookStatus.LOANED
        }
      ]);

      this.isLoading.set(false);
    }, 400);
  }
}