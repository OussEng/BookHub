import { Component, inject, signal } from '@angular/core';
import { BookService } from '../../../../services/book-service/book.service';

// import { AuthorService } from '../../../../services/author-service/author.service';
// import { GenreService } from '../../../../services/genre-service/genre.service';

import { Book } from '../../../../interfaces/book/Book';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookCreateModal } from '../../../../components/modals/book-create-modal/book-create-modal';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Author } from '../../../../interfaces/author/author';
import { Genre } from '../../../../interfaces/genre/genre';
import { AuthorService } from '../../../../services/author-service/author.service';
import { GenreService } from '../../../../services/genre-service/genre.service';
import { FlashMessageService } from '../../../../services/flash-message-service/flash-message-service';
import { BookUpdateModal } from '../../../../components/modals/book-update-modal/book-update-modal';
import { BookDeleteModal } from '../../../../components/modals/book-delete-modal/book-delete-modal';

@Component({
  selector: 'app-librarian-book-catalogue',
  standalone: true,
  imports: [CommonModule, RouterLink, MatDialogModule],
  templateUrl: './librarian-book-catalogue.html',
  styleUrl: './librarian-book-catalogue.css',
})
export class LibrarianBookCatalogue {
  private bookService = inject(BookService);
  private dialog = inject(MatDialog);
  private flashService = inject(FlashMessageService);
  
  
  private authorService = inject(AuthorService);
  private genreService = inject(GenreService);

  protected authors = signal<Author[]>([]);
  protected genres = signal<Genre[]>([]);



  protected books = signal<Book[]>([]);
  protected isLoading = signal<boolean>(true);


  protected currentPage = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected totalElements = signal<number>(0);
  protected readonly pageSize = 20;

  
  protected searchInput = signal<string>('');
  protected selectedGenreId = signal<number | null>(null);
  protected sortBy = signal<string>('id');
  protected sortDir = signal<string>('desc');



  ngOnInit(): void {
    this.loadBooks(0);
    this.loadDropdownData();
  }

  loadBooks(page: number): void {
    this.isLoading.set(true);
    this.bookService
      .getBooks(
        page,
        this.pageSize,
        this.searchInput(),
        this.selectedGenreId() ?? undefined,
        this.sortBy(),
        this.sortDir()
      )
      .subscribe({
        next: (pageData) => {
          this.books.set(pageData.content);
          this.currentPage.set(pageData.number);
          this.totalPages.set(pageData.totalPages);
          this.totalElements.set(pageData.totalElements);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Error fetching books:', err);
          this.isLoading.set(false);
        },
      });
  }

  onSearch(query: string, genreVal: string, sortVal: string): void {
    this.searchInput.set(query);
    this.selectedGenreId.set(genreVal ? Number(genreVal) : null);

    if (sortVal) {
      const [field, dir] = sortVal.split(',');
      this.sortBy.set(field);
      this.sortDir.set(dir);
    }

    this.loadBooks(0);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.loadBooks(page);
    }
  }

   get endIndex(): number {
    return Math.min((this.currentPage() + 1) * this.pageSize, this.totalElements());
  }

  
  loadDropdownData() {
    this.authorService.getAuthors().subscribe(authors => this.authors.set(authors));
    this.genreService.getGenres().subscribe(genres => this.genres.set(genres));
  }
  

  openNewBookModal(): void {
    const dialogRef = this.dialog.open(BookCreateModal, {
      width: '500px',
      data: {
        authors: this.authors(), 
        genres: this.genres()
      }
    });

    dialogRef.afterClosed().subscribe(result => {
  if (result) {
    const { imageFile, authors, genres, ...rest } = result;

    const bookData = {
      ...rest,
      authorIds: authors,
      genreIds: genres
    };

    const formData = new FormData();
    formData.append('book', new Blob([JSON.stringify(bookData)], { type: 'application/json' }));
    if (imageFile) {
      formData.append('image', imageFile);
    }

    this.bookService.createBookWithImage(formData).subscribe({
      next: (newBook) => {
        this.loadBooks(0);

        this.flashService.success("Livre créé avec succès !")
      },
      error: (err) => console.error('Error uploading book & image', err)
    });
  }
});
    
  }


  openUpdateBookModal(bookId: number): void {
  if (this.authors().length === 0 || this.genres().length === 0) {
    this.loadDropdownData();
  }

  this.bookService.getBookById(bookId).subscribe({
    next: (bookDetail) => {
      const dialogRef = this.dialog.open(BookUpdateModal, {
        width: '500px',
        data: {
          bookDetail,
          authors: this.authors(),
          genres: this.genres()
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          const { imageFile, authors, genres, ...rest } = result;

          const bookData = {
            ...rest,
            authorIds: authors,
            genreIds: genres
          };

          const formData = new FormData();
          formData.append('book', new Blob([JSON.stringify(bookData)], { type: 'application/json' }));
          
          if (imageFile) {
            formData.append('image', imageFile);
          }

          this.bookService.updateBookWithImage(bookId, formData).subscribe({
            next: () => {
              this.loadBooks(this.currentPage());
              this.flashService.success("Livre mis à jour avec succès !");
            },
            error: (err) => console.error('Error updating book', err)
          });
        }
      });
    },
    error: (err) => console.error('Error loading book details', err)
  });
  }

  openDeleteBookModal(book: Book): void {
    const dialogRef = this.dialog.open(BookDeleteModal, {
      data: { book },
      width: '440px',
      panelClass: 'custom-dialog-container'
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.deleteBook(book.id);
        this.flashService.success("Livre supprimé avec succès");
      }
    });
  }

  private deleteBook(id: number): void {
    this.bookService.delete(id).subscribe({
      next: () => {
        this.loadBooks(0); 
      },
      error: () => {
        console.error('Erreur lors de la suppression de l’ouvrage:');
      }
    });
  }
}