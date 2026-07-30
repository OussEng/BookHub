import { Component, OnInit, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookService } from '../../services/book-service/book.service';
import { LoansService } from "../../services/loans.service";
import { BookActionResponse } from "../../interfaces/reservation/response/book-action.model";

import { BookReviews } from "../book-reviews/book-reviews";
import { FlashMessageService } from "../../services/flash-message-service/flash-message-service";
import {ReservationService} from "../../services/reservation.service";

@Component({
    selector: 'app-book-details',
    standalone: true,
    imports: [CommonModule, RouterLink, BookReviews],
    templateUrl: './book-details.html',
    styleUrl: './book-details.css',
})
export class BookDetails implements OnInit {
    private bookService = inject(BookService);
    private loanService = inject(LoansService);
    private reservationService = inject(ReservationService);
    private flashService = inject(FlashMessageService);

    defaultCoverUrl = 'assets/images/default.png';

    id = input.required<string>();

    book: any = null;
    protected isLoading = signal<boolean>(true);
    protected isActionLoading = signal<boolean>(false);
    protected bookAction = signal<BookActionResponse | null>(null);
    errorMessage = '';

    ngOnInit() {
        const bookId = Number(this.id());

        this.loadBooks(bookId);
        this.loadAction(bookId);
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
            }
        });
    }

    loadAction(bookId: number) {
        this.reservationService.getAvailableAction(bookId).subscribe({
            next: (data) => {
                this.bookAction.set(data);
            },
            error: (err) => {
                console.error('Erreur lors du chargement du statut du livre', err);
            }
        });
    }

    handleAction() {
        const action = this.bookAction();
        if (!action) return;

        const bookId = Number(this.id());

        switch (action.action) {
            case 'LOAN':
                this.loan(bookId);
                break;
            case 'RESERVE':
                this.reserve(bookId);
                break;
            case 'CANCEL':
                if (action.reservationId) {
                    this.cancel(action.reservationId, bookId);
                }
                break;
            case 'PICKUP':
                this.loan(bookId);
                break;
        }
    }

    private loan(bookId: number) {
        this.isActionLoading.set(true);

        this.loanService.newLoan(bookId).subscribe({
            next: () => {
                this.loadBooks(bookId);
                this.loadAction(bookId);
                this.flashService.success("Votre location a bien été enregistrée");
                this.isActionLoading.set(false);
            },
            error: (err) => {
                this.isActionLoading.set(false);
                this.flashService.error("Impossible d'emprunter ce livre");
            }
        });
    }

    private reserve(bookId: number) {
        this.isActionLoading.set(true);

        this.reservationService.reserver(bookId).subscribe({
            next: () => {
                this.loadAction(bookId);
                this.flashService.success("Votre réservation a bien été enregistrée");
                this.isActionLoading.set(false);
            },
            error: (err) => {
                this.isActionLoading.set(false);
                this.flashService.error("Impossible de réserver ce livre");
            }
        });
    }

    private cancel(reservationId: number, bookId: number) {
        this.isActionLoading.set(true);

        this.reservationService.annuler(reservationId).subscribe({
            next: () => {
                this.loadAction(bookId);
                this.flashService.success("Votre réservation a été annulée");
                this.isActionLoading.set(false);
            },
            error: (err) => {
                this.isActionLoading.set(false);
                this.flashService.error("Impossible d'annuler la réservation");
            }
        });
    }

    handleImageError(event: Event): void {
        const img = event.target as HTMLImageElement;
        img.src = this.defaultCoverUrl;
    }
}