import {Component, inject, input, signal} from '@angular/core';
import {CommonModule} from "@angular/common";
import {RouterLink} from "@angular/router";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {BookService} from "../../../../services/book-service/book.service";
import {BookCopyService} from "../../../../services/book-copy/book-copy.service";
import {FlashMessageService} from "../../../../services/flash-message-service/flash-message-service";
import {BookCopy} from "../../../../interfaces/book-copy/book-copy";
import {BookCondition} from "../../../../interfaces/book-copy/book-copy-condition";
import {BookCopyCreateModal} from "../../../../components/modals/book-copy-create-modal/book-copy-create-modal";
import {CreateBookCopyDto} from "../../../../interfaces/book-copy/book-copy-create";
import {LoansService} from "../../../../services/loans.service";
import {LoansResponseModel} from "../../../../interfaces/loans/loans-response-model";
import {LoanStatusPipe} from "../../../../pipes/loan-status-pipe";

@Component({
    selector: 'app-librarian-loans',
    imports: [CommonModule, MatDialogModule, LoanStatusPipe],
    templateUrl: './librarian-loans.html',
    styleUrl: './librarian-loans.css',
})
export class LibrarianLoans {
    private loansService: LoansService = inject(LoansService);
    private flashService: FlashMessageService = inject(FlashMessageService);

    id = input.required<string>();

    loans: LoansResponseModel[] = [];
    protected isLoading = signal<boolean>(true);
    errorMessage = '';

    ngOnInit() {

        this.loadLoans();

    }

    loadLoans() {
        this.loansService.getLoans().subscribe({
            next: (data) => {
                this.loans = data.reverse();
                this.isLoading.set(false)
            },
            error: (err) => {
                this.errorMessage = "Impossible de charger les emprunts.";
                console.error(err);
                this.isLoading.set(false)
            }
        });
    }

    returnALoan(loanId: number) {
        this.isLoading.set(true);

        this.loansService.returnLoan(loanId).subscribe({
            next: (data) => {
                this.loadLoans()
                this.flashService.success("Emprunt retourné avec succès.");
                this.isLoading.set(false);
            },
            error: (err) => {
                this.errorMessage = "Impossible de retourner l'emprunt.";
                console.error(err);
                this.isLoading.set(false);
            }
        });
    }
}
