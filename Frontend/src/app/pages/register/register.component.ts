import { Component, signal } from '@angular/core';
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

  errorMessage = signal('');
  successMessage = signal('');
  isLoading = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.isLoading.set(true);

    this.authService.register({
      username: this.username,
      firstname: this.firstname,
      lastname: this.lastname,
      phone: this.phone,
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Account created. Redirecting to login...');

        setTimeout(() => this.router.navigate(['/login']), 1200);
      },
      error: (err) => {
        this.isLoading.set(false);

        this.errorMessage.set(
          err.status === 409
            ? err.error.error
            : 'Something went wrong. Please try again.'
        );
      }
    });
  }
}