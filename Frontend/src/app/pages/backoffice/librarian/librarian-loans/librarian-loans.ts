import {Component, computed, inject, signal} from '@angular/core';
import {CommonModule} from "@angular/common";
import {LoansService} from "../../../../services/loans.service";
import {LoansResponseModel} from "../../../../interfaces/loans/loans-response-model";
import {LoanStatusPipe} from "../../../../pipes/loan-status-pipe";
import {FlashMessageService} from "../../../../services/flash-message-service/flash-message-service";

@Component({
    selector: 'app-librarian-loans',
    standalone: true,
    imports: [CommonModule, LoanStatusPipe],
    templateUrl: './librarian-loans.html',
    styleUrl: './librarian-loans.css',
})
export class LibrarianLoans {
    private loansService: LoansService = inject(LoansService);
    private flashService: FlashMessageService = inject(FlashMessageService);

    protected allLoans = signal<LoansResponseModel[]>([]);
    protected isLoading = signal<boolean>(true);
    protected errorMessage = signal<string>('');

    protected searchInput = signal<string>('');
    protected currentPage = signal<number>(0);
    protected readonly pageSize = 20;

    protected filteredLoans = computed(() => {
        const query = this.searchInput().trim().toLowerCase();
        if (!query) {
            return this.allLoans();
        }

        return this.allLoans().filter(loan => {
            const bookTitle = loan.bookTitle?.toLowerCase() ?? '';
            const loanerName = `${loan.loanerFirstName} ${loan.loanerLastName}`.toLowerCase();
            return bookTitle.includes(query) || loanerName.includes(query);
        });
    });

    protected totalElements = computed(() => this.filteredLoans().length);

    protected totalPages = computed(() => Math.max(1, Math.ceil(this.totalElements() / this.pageSize)));

    protected paginatedLoans = computed(() => {
        const start = this.currentPage() * this.pageSize;
        return this.filteredLoans().slice(start, start + this.pageSize);
    });

    ngOnInit() {
        this.loadLoans();
    }

    loadLoans() {
        this.isLoading.set(true);
        this.loansService.getLoans().subscribe({
            next: (data) => {
                this.allLoans.set(data.reverse());
                this.isLoading.set(false);
            },
            error: (err) => {
                this.errorMessage.set("Impossible de charger les emprunts.");
                console.error(err);
                this.isLoading.set(false);
            }
        });
    }

    onSearch(query: string): void {
        this.searchInput.set(query);
        this.currentPage.set(0);
    }

    goToPage(page: number): void {
        if (page >= 0 && page < this.totalPages()) {
            this.currentPage.set(page);
        }
    }

    returnALoan(loanId: number) {
        this.isLoading.set(true);

        this.loansService.returnLoan(loanId).subscribe({
            next: () => {
                this.flashService.success("Emprunt retourné avec succès.");
                setTimeout(() => this.loadLoans());
            },
            error: (err) => {
                this.errorMessage.set("Impossible de retourner l'emprunt.");
                console.error(err);
                this.isLoading.set(false);
            }
        });
    }

    isLate(loan: LoansResponseModel): boolean {
        if (loan.status === 'RETURNED') {
            return false;
        }
        return new Date() > new Date(loan.dueDate);
    }

    wasReturnedLate(loan: LoansResponseModel): boolean {
        if (loan.status !== 'RETURNED' || !loan.returnDate) {
            return false;
        }
        return new Date(loan.returnDate) > new Date(loan.dueDate);
    }
}