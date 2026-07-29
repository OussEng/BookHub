import { Component } from '@angular/core';
import { Book } from '../../../../interfaces/book/Book';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-librarian-home',
  imports: [CommonModule],
  templateUrl: './librarian-home.html',
  styleUrl: './librarian-home.css',
})
export class LibrarianHome {

  totalBooks = 14250;
  activeLoans = 1240;
  overdueLoans = 85;
  pendingReservations = 42;

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
    { ...this.sampleBook, id: 1, title: 'Les Misérables', author: ['Victor Hugo'], loans: 145 },
    { ...this.sampleBook, id: 2, title: 'Le Petit Prince', author: ['Antoine de Saint-Exupéry'], loans: 132 },
    { ...this.sampleBook, id: 3, title: 'L’Étranger', author: ['Albert Camus'], loans: 118 },
    { ...this.sampleBook, id: 4, title: '1984', author: ['George Orwell'], loans: 105 },
    { ...this.sampleBook, id: 5, title: 'Harry Potter', author: ['J.K. Rowling'], loans: 98 },
    { ...this.sampleBook, id: 6, title: 'Le Comte de Monte-Cristo', author: ['Alexandre Dumas'], loans: 91 },
    { ...this.sampleBook, id: 7, title: 'La Peste', author: ['Albert Camus'], loans: 84 },
    { ...this.sampleBook, id: 8, title: 'Bel-Ami', author: ['Guy de Maupassant'], loans: 79 },
    { ...this.sampleBook, id: 9, title: 'Madame Bovary', author: ['Gustave Flaubert'], loans: 73 },
    { ...this.sampleBook, id: 10, title: 'Germinal', author: ['Émile Zola'], loans: 68 }
  ];
}