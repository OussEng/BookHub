import { Component, OnInit, signal } from '@angular/core';
import { BookService } from '../../../services/book-service/book.service';
import { Book } from '../../../interfaces/book/Book';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  
  constructor(private bookService : BookService){}

  protected books = signal<Book[]>([]);
  protected isLoading = signal<boolean>(true);



  ngOnInit(): void {
    this.bookService.getBooks().subscribe(
      {
        next: (books) =>{
          this.isLoading.set(false);
          this.books.set(books)
          console.log(books)
        } 
      }
    )
  }

}
