import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CurrentUser } from '../../interfaces/current user/current-user';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class profilComponent implements OnInit {
  user: CurrentUser | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }


}