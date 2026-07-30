import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Author } from '../../../interfaces/author/author';
import { Genre } from '../../../interfaces/genre/genre';
import { BookUpdateModalData } from '../../../interfaces/book-update-modal-data/BookUpdateModalData';




@Component({
  selector: 'app-book-update-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
  templateUrl: './book-update-modal.html',
})
export class BookUpdateModal implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<BookUpdateModal>);
  private data: BookUpdateModalData = inject(MAT_DIALOG_DATA);

  availableAuthors: Author[] = [];
  availableGenres: Genre[] = [];
  imagePreview: string | ArrayBuffer | null = null;
  selectedFile: File | null = null;

  bookForm: FormGroup = this.fb.group({
    title: ['', Validators.required],
    authors: [[], Validators.required],
    genres: [[], Validators.required],
    isbn: ['', Validators.required],
    description: ['', Validators.required],
    publishDate: ['', Validators.required]
  });

  ngOnInit(): void {
    if (this.data) {
      this.availableAuthors = this.data.authors || [];
      this.availableGenres = this.data.genres || [];

      const book = this.data.bookDetail;
      if (book) {
        this.bookForm.patchValue({
          title: book.title,
          authors: book.authorIds,
          genres: book.genreIds,
          isbn: book.isbn,
          description: book.description,
          publishDate: book.publishDate
        });

        if (book.img) {
          this.imagePreview = book.img;
        }
      }
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.bookForm.valid) {
      const formResult = {
        ...this.bookForm.value,
        imageFile: this.selectedFile
      };
      this.dialogRef.close(formResult);
    }
  }
}