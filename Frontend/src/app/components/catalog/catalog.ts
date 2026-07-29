import {Component, OnInit} from '@angular/core';
import {Book} from "../../interfaces/book/book";
import {BookService} from "../../services/book.service";

@Component({
  selector: 'app-catalog',
  imports: [],
  templateUrl: './catalog.html',
  styleUrl: './catalog.css',
})
export class Catalog implements OnInit {
  books: Book[] = [];

  constructor(private bookService: BookService) {
  }

  ngOnInit(): void {
    this.bookService.getAllBooks().subscribe(data => {
      this.books = data
    });
  }
}