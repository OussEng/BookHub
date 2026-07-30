import { Component, OnInit, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookService } from '../../services/book-service/book.service';
import {LoansService} from "../../services/loans.service";
import {FlashMessageService} from "../../services/flash-message-service/flash-message-service";

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
    defaultCoverUrl = 'assets/images/default.png';
    private flashService = inject(FlashMessageService);

  id = input.required<string>();

    book: any = null;
    loans: any = [];
    protected isLoading = signal<boolean>(true);
    protected isLoaned= signal<boolean>(false);
    errorMessage = '';

    ngOnInit() {
        const bookId = Number(this.id());

        this.loadBooks(bookId);
        this.loadLoans();
    }

    loadBooks(bookId: number) {
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

    loadLoans() {
        this.loanService.getMyLoans().subscribe({
            next: (data) => {
                this.loans = data;
                this.loanService.isBookAlreadyLoanedByUser(this.loans, this.book).subscribe({
                    next: (isLoaned) => {
                        this.isLoaned.set(isLoaned);
                    }
                })
            }
        })
    }

    /*
    Method in charge to loan a book_copy by the book id loaded in the page detail.
     */
    loanBookCopyByBookDetails(){
        this.isLoading.set(true);

        if (this.book.available) {
            const bookId = this.book.id;
            this.loanService.newLoan(bookId).subscribe({
                next: (data) => {
                    this.loadBooks(bookId);
                    this.loadLoans();
                    this.isLoading.set(false)
                    this.flashService.success("Votre location a bien été enregistrée")
                },
                error: (err) => {
                    this.isLoading.set(false)
                    this.errorMessage = "Impossible d'emprunter ce livre";
                    this.flashService.error("Impossible d'emprunter ce livre");
                }
            });
        }
    }

    handleImageError(event: Event): void {
        const img = event.target as HTMLImageElement;
        img.src = this.defaultCoverUrl;
    }
}