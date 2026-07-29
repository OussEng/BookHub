import {Component, OnInit, inject, input, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {BookService} from '../../services/book-service/book.service';
import {LoansService} from "../../services/loans.service";

@Component({
    selector: 'app-book-details',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './book-details.html',
    styleUrl: './book-details.css',
})
export class BookDetails implements OnInit {
    private bookService = inject(BookService);
    private loanService = inject(LoansService);

    id = input.required<string>();

    book: any = null;
    protected isLoading = signal<boolean>(true);
    errorMessage = '';

    ngOnInit() {
        const bookId = Number(this.id());

        this.bookService.getBookById(bookId).subscribe({
            next: (data) => {
                this.book = data;
                this.isLoading.set(false)
            },
            error: (err) => {
                this.errorMessage = "Impossible de charger les détails du livre.";
                this.isLoading.set(false)
                console.error(err);
            }
        });
    }

    //Action du bouton "Emprunter le livre" -> Recherche tous les exemplaires pour en emprunter un si dispo.
    loanBookCopyByBookDetails(){
        if (this.book.available) {
            const bookId = this.book.id;
            this.loanService.newLoan(bookId);
        }
    }
}