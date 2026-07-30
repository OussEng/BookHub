import { Component } from '@angular/core';
import { Book } from '../../../../interfaces/book/Book';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-librarian-home',
  imports: [CommonModule, RouterLink],
  templateUrl: './librarian-home.html',
  styleUrl: './librarian-home.css',
})
export class LibrarianHome {

  totalBooks = 60;
  activeLoans = 90;
  overdueLoans = 30;
  pendingReservations = 20;

  sampleBook: Book = {
    id: 1,
    title: "Dune",
    img: "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=300",
    description: "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides...",
    isbn: "978-0441172719",
    genres: ["Science Fiction"],
    publishDate: "1965-08-01",
    author: ["Frank Herbert"],
    available: false
  };

  topBooks: (Book & { loans: number })[] = [
    { ...this.sampleBook, id: 1, title: 'Les Misérables', author: ['Victor Hugo'], loans: 20 },
    { ...this.sampleBook, id: 2, title: 'Le Petit Prince', author: ['Antoine de Saint-Exupéry'], loans: 19 },
    { ...this.sampleBook, id: 3, title: 'L’Étranger', author: ['Albert Camus'], loans: 15 },
    { ...this.sampleBook, id: 4, title: '1984', author: ['George Orwell'], loans: 12 },
    { ...this.sampleBook, id: 5, title: 'Harry Potter', author: ['J.K. Rowling'], loans: 11 },
    { ...this.sampleBook, id: 6, title: 'Le Comte de Monte-Cristo', author: ['Alexandre Dumas'], loans: 11 },
    { ...this.sampleBook, id: 7, title: 'La Peste', author: ['Albert Camus'], loans: 10 },
    { ...this.sampleBook, id: 8, title: 'Bel-Ami', author: ['Guy de Maupassant'], loans: 10 },
    { ...this.sampleBook, id: 9, title: 'Madame Bovary', author: ['Gustave Flaubert'], loans: 10 },
    { ...this.sampleBook, id: 10, title: 'Germinal', author: ['Émile Zola'], loans: 9 }
  ];
}