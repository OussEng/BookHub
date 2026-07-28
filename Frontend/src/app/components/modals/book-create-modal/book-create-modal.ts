import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Author } from '../../../interfaces/author/author';
import { Genre } from '../../../interfaces/genre/genre';

@Component({
  selector: 'app-book-create-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
  templateUrl: './book-create-modal.html',
  styleUrl: './book-create-modal.css',
})
export class BookCreateModal implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<BookCreateModal>);
  private data = inject(MAT_DIALOG_DATA, { optional: true });

  availableAuthors: Author[] = [];
  availableGenres: Genre[] = [];
  imagePreview: string | ArrayBuffer | null = null;
  selectedFile: File | null = null;

  bookForm: FormGroup = this.fb.group({
    title: ['', Validators.required],
    authors: [[], Validators.required],
    genres: [[], Validators.required],
    isbn: ['', Validators.required],
    description: [''],
    publishDate: [new Date().toISOString().split('T')[0]]
  });

  ngOnInit(): void {
    if (this.data) {
      this.availableAuthors = this.data.authors || [];
      this.availableGenres = this.data.genres || [];
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