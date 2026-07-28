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
  
  
  // private authorService = inject(AuthorService);
  // private genreService = inject(GenreService);

  protected books = signal<Book[]>([]);
  protected isLoading = signal<boolean>(true);

  // MOCK DATA (Remove or let API override these once ready)
  protected mockAuthors = signal<Author[]>([
    { id: 1, firstname: 'Victor', lastname: 'Hugo' },
    { id: 2, firstname: 'Albert', lastname: 'Camus' },
    { id: 3, penName: 'Molière' }
  ]);

  protected mockGenres = signal<Genre[]>([
    { id: 1, label: 'Roman' },
    { id: 2, label: 'Théâtre' },
    { id: 3, label: 'Philosophie' }
  ]);

  ngOnInit(): void {
    this.loadBooks();
    
    
    // this.loadDropdownData();
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

  /*
  loadDropdownData() {
    this.authorService.getAuthors().subscribe(authors => this.mockAuthors.set(authors));
    this.genreService.getGenres().subscribe(genres => this.mockGenres.set(genres));
  }
  */

  openNewBookModal(): void {
    const dialogRef = this.dialog.open(BookCreateModal, {
      width: '500px',
      data: {
        authors: this.mockAuthors(), 
        genres: this.mockGenres()
      }
    });

    dialogRef.afterClosed().subscribe(result => {
  if (result) {
    
    const { imageFile, ...bookData } = result;

    /*
    const formData = new FormData();
    formData.append('book', new Blob([JSON.stringify(bookData)], { type: 'application/json' }));
    if (imageFile) {
      formData.append('image', imageFile); // 'image' should match your Spring Boot MultipartFile parameter name
    }

    this.bookService.createBookWithImage(formData).subscribe({
      next: (newBook) => {
        console.log('Book and image created successfully in DB!', newBook);
        this.loadBooks();
      },
      error: (err) => console.error('Error uploading book & image', err)
    });
    */

    console.log('Form data to send to server:', bookData);
    console.log('Image file object to upload:', imageFile);
  }
});
  }
}