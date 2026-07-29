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



  ngOnInit(): void {
    this.loadBooks();
    
    
    this.loadDropdownData();
  }

  loadBooks(): void {
   
    this.bookService.getBooks().subscribe({
      next: (books) => {
        this.isLoading.set(false);
        this.books.set(books);
      },
      error: (err) => console.error('Error loading books', err)
    });
  
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
        this.loadBooks();

        this.flashService.success("Livre créé avec succès !")
      },
      error: (err) => console.error('Error uploading book & image', err)
    });
  }
});
    
  }
}