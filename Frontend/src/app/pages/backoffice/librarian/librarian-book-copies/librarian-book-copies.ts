import { Component, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookService } from '../../../../services/book-service/book.service';
import { BookCopyService } from '../../../../services/book-copy/book-copy.service';
import { BookCopy } from '../../../../interfaces/book-copy/book-copy';
import { BookCondition } from '../../../../interfaces/book-copy/book-copy-condition';
import { BookStatus } from '../../../../interfaces/book-copy/book-copy-status';
import { BookCopyCreateModal } from '../../../../components/modals/book-copy-create-modal/book-copy-create-modal';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { CreateBookCopyDto } from '../../../../interfaces/book-copy/book-copy-create';
import { FlashMessageService } from '../../../../services/flash-message-service/flash-message-service';


@Component({
  selector: 'app-librarian-book-copies',
  standalone: true,
  imports: [CommonModule, RouterLink, MatDialogModule],
  templateUrl: './librarian-book-copies.html',
  styleUrl: './librarian-book-copies.css',
})
export class LibrarianBookCopies {
  private bookService = inject(BookService);
  private bookCopyService = inject(BookCopyService); 
  private dialog = inject(MatDialog);
  private flashService = inject(FlashMessageService);

  id = input.required<string>();

  book: any = null;
  protected copies = signal<BookCopy[]>([]);
  protected isLoading = signal<boolean>(true);
  errorMessage = '';

  ngOnInit() {
    const bookId = Number(this.id());

  
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
  }


  openNewBookModal(): void {
      const dialogRef = this.dialog.open(BookCopyCreateModal, {
        width: '500px',
        data: {}
      });
  
      dialogRef.afterClosed().subscribe((formData) => {
      if (formData) {
        const payload: CreateBookCopyDto = {
          ...formData,
          bookId: this.id(),
        };
      
        this.bookCopyService.createBookCopy(payload).subscribe({
          next: (createdCopy) => {
            this.copies.update((current) => [...current, createdCopy]);

            this.flashService.success("Exemplaire créé avec succès !")

          },
          error: (err) => {
            this.errorMessage = "Impossible de créer l'exemplaire.";
            console.error(err);
          },
        });
      }
    });
}
}