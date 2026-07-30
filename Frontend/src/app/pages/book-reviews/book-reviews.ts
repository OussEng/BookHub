import {Component, inject, input, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ReviewResponseModel} from "../../interfaces/review/response/review-response.model";
import {ReviewService} from "../../services/review-service/review.service";

@Component({
  selector: 'app-book-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book-reviews.html',
  styleUrl: './book-reviews.css',
})
export class BookReviews implements OnInit {
  private reviewService = inject(ReviewService);
  protected authService = inject(AuthService);

  bookId = input.required<number>();

  reviews = signal<ReviewResponseModel[]>([]);
  currentPage = signal(0);
  totalPages = signal(0);
  isLoading = signal(true);
  errorMessage = signal('');

  newRating = signal(0);
  newComment = signal('');
  isSubmitting = signal(false);

  editingReviewId = signal<number | null>(null);
  editRating = signal(0);
  editComment = signal('');

  ngOnInit() {
    this.loadReviews(0);
  }

  loadReviews(page: number) {
    this.isLoading.set(true);
    this.reviewService.getReviewsByBook(this.bookId(), page).subscribe({
      next: (data) => {
        this.reviews.set(data.content);
        this.currentPage.set(data.number);
        this.totalPages.set(data.totalPages);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Impossible de charger les avis.');
        this.isLoading.set(false);
      }
    });
  }

  submitReview() {
    if (this.newRating() < 0 || this.newRating() > 5) return;

    this.isSubmitting.set(true);
    this.reviewService.createReview(this.bookId(), {
      rating: this.newRating(),
      comment: this.newComment() || null
    }).subscribe({
      next: () => {
        this.newRating.set(0);
        this.newComment.set('');
        this.isSubmitting.set(false);
        this.loadReviews(0);
      },
      error: () => {
        this.isSubmitting.set(false);
      }
    });
  }

  startEdit(review: ReviewResponseModel) {
    this.editingReviewId.set(review.id);
    this.editRating.set(review.rating);
    this.editComment.set(review.comment ?? '');
  }

  cancelEdit() {
    this.editingReviewId.set(null);
  }

  submitEdit(reviewId: number) {
    this.reviewService.updateReview(this.bookId(), reviewId, {
      rating: this.editRating(),
      comment: this.editComment() || null
    }).subscribe({
      next: () => {
        this.editingReviewId.set(null);
        this.loadReviews(this.currentPage());
      }
    });
  }

  deleteReview(reviewId: number) {
    this.reviewService.deleteReview(this.bookId(), reviewId).subscribe({
      next: () => this.loadReviews(this.currentPage())
    });
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.loadReviews(page);
    }
  }

  currentUserId = this.authService.getCurrentUser()?.id ?? null;

  isOwnReview(review: ReviewResponseModel): boolean {
    return this.currentUserId !== null && this.currentUserId === review.userId;
  }
}