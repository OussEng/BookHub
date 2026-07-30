import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AdminReviewService } from '../../../../services/admin-review-service/admin-review.service';
import { FlashMessageService } from '../../../../services/flash-message-service/flash-message-service';
import { ReviewDeleteModal } from '../../../../components/modals/review-delete-modal/review-delete-modal';
import {AdminReviewResponse} from "../../../../interfaces/review/response/admin-review-response";

@Component({
  selector: 'app-librarian-reviews',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './librarian-reviews.html',
  styleUrl: './librarian-reviews.css',
})
export class LibrarianReviews {
  private reviewService = inject(AdminReviewService);
  private dialog = inject(MatDialog);
  private flashService = inject(FlashMessageService);

  protected reviews = signal<AdminReviewResponse[]>([]);
  protected isLoading = signal<boolean>(true);

  protected currentPage = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected totalElements = signal<number>(0);
  protected readonly pageSize = 20;

  protected searchInput = signal<string>('');

  ngOnInit(): void {
    this.loadReviews(0);
  }

  loadReviews(page: number): void {
    this.isLoading.set(true);
    this.reviewService
        .getReviews(page, this.pageSize, this.searchInput())
        .subscribe({
          next: (pageData) => {
            this.reviews.set(pageData.content);
            this.currentPage.set(pageData.number);
            this.totalPages.set(pageData.totalPages);
            this.totalElements.set(pageData.totalElements);
            this.isLoading.set(false);
          },
          error: (err) => {
            console.error('Error fetching reviews:', err);
            this.isLoading.set(false);
          },
        });
  }

  onSearch(query: string): void {
    this.searchInput.set(query);
    this.loadReviews(0);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.loadReviews(page);
    }
  }

  openDeleteReviewModal(review: AdminReviewResponse): void {
    const dialogRef = this.dialog.open(ReviewDeleteModal, {
      data: { review },
      width: '440px',
      panelClass: 'custom-dialog-container'
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.reviewService.moderateReview(review.id).subscribe({
          next: () => {
            this.loadReviews(this.currentPage());
            this.flashService.success("Commentaire supprimé avec succès");
          },
          error: (err) => console.error('Error moderating review', err)
        });
      }
    });
  }
}