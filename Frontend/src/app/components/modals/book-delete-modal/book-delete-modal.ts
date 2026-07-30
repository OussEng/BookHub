import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Book } from '../../../interfaces/book/Book';

@Component({
  selector: 'app-book-delete-modal',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './book-delete-modal.html'
})
export class BookDeleteModal {
  private dialogRef = inject(MatDialogRef<BookDeleteModal>);
  public data: { book: Book } = inject(MAT_DIALOG_DATA);

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}