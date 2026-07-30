import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import {AdminReviewResponse} from "../../../interfaces/review/response/admin-review-response";

@Component({
  selector: 'app-review-delete-modal',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './review-delete-modal.html'
})
export class ReviewDeleteModal {
  private dialogRef = inject(MatDialogRef<ReviewDeleteModal>);
  public data: { review: AdminReviewResponse } = inject(MAT_DIALOG_DATA);

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}