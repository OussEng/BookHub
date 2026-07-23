import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  username = '';
  firstname = '';
  lastname = '';
  phone = '';
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;

    this.authService.register({
      username: this.username,
      firstname: this.firstname,
      lastname: this.lastname,
      phone: this.phone,
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Account created. Redirecting to login...';
        setTimeout(() => this.router.navigate(['/login']), 1200);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.status === 409
          ? 'An account with this email already exists.'
          : 'Something went wrong. Please try again.';
      }
    });
  }
}