import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { BookCondition } from '../../../interfaces/book-copy/book-copy-condition';
import { BookStatus } from '../../../interfaces/book-copy/book-copy-status';


@Component({
  selector: 'app-book-copy-create-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
  templateUrl: './book-copy-create-modal.html',
  styleUrl: './book-copy-create-modal.css',
})
export class BookCopyCreateModal implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<BookCopyCreateModal>);
  private data = inject<{ bookId: number }>(MAT_DIALOG_DATA, { optional: true });


  conditions = Object.values(BookCondition);
  statuses = Object.values(BookStatus);

  copyForm: FormGroup = this.fb.group({
    serialNumber: ['', Validators.required],
    condition: [this.conditions[0] || '', Validators.required],
  });

  ngOnInit(): void {
    if (this.data?.bookId) {
      this.copyForm.patchValue({ bookId: this.data.bookId });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.copyForm.valid) {
      this.dialogRef.close(this.copyForm.value);
    }
  }


  
}