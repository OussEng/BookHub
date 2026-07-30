import { Component, inject, input, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookService } from '../../../../services/book-service/book.service';
import { BookCopyService } from '../../../../services/book-copy/book-copy.service';
import { BookCopy } from '../../../../interfaces/book-copy/book-copy';
import { BookCondition } from '../../../../interfaces/book-copy/book-copy-condition';
import { BookCopyCreateModal } from '../../../../components/modals/book-copy-create-modal/book-copy-create-modal';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { CreateBookCopyDto } from '../../../../interfaces/book-copy/book-copy-create';
import { FlashMessageService } from '../../../../services/flash-message-service/flash-message-service';

@Component({
  selector: 'app-librarian-book-copies',
  standalone: true,
  imports: [CommonModule, RouterLink, MatDialogModule],
  templateUrl: './librarian-book-copies.html',
  styleUrl: './librarian-book-copies.css',
})
export class LibrarianBookCopies implements OnInit {
  private bookService = inject(BookService);
  private bookCopyService = inject(BookCopyService); 
  private dialog = inject(MatDialog);
  private flashService = inject(FlashMessageService);

  id = input.required<string>();

  book: any = null;
  protected copies = signal<BookCopy[]>([]);
  protected isLoading = signal<boolean>(true);
  errorMessage = '';

  protected currentPage = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected totalElements = signal<number>(0);
  protected readonly pageSize = 20;

  protected serialNumberFilter = signal<string>('');
  protected conditionFilter = signal<BookCondition | undefined>(undefined);

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

    this.loadCopies(0);
  }

  loadCopies(page: number): void {
    this.isLoading.set(true);
    const bookId = Number(this.id());

    this.bookCopyService
      .getBookCopiesByBookId(
        bookId,
        page,
        this.pageSize,
        this.serialNumberFilter(),
        this.conditionFilter()
      )
      .subscribe({
        next: (pageData) => {
          this.copies.set(pageData.content);
          this.currentPage.set(pageData.number);
          this.totalPages.set(pageData.totalPages);
          this.totalElements.set(pageData.totalElements);
          this.isLoading.set(false);
        },
        error: (err) => {
          this.errorMessage = "Impossible de charger les exemplaires.";
          this.isLoading.set(false);
          console.error(err);
        }
      });
  }

  onSearch(serialNumberVal: string, conditionVal: string): void {
    const cleanSerial = serialNumberVal ? serialNumberVal.trim() : '';
    const cleanCondition = conditionVal && conditionVal !== '' ? (conditionVal as BookCondition) : undefined;

    this.serialNumberFilter.set(cleanSerial);
    this.conditionFilter.set(cleanCondition);
    
    this.loadCopies(0);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.loadCopies(page);
    }
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
          next: () => {
            this.flashService.success("Exemplaire créé avec succès !");
            this.loadCopies(this.currentPage());
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