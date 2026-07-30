import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CurrentUser } from '../../interfaces/current user/current-user';
import {LoansResponseModel} from "../../interfaces/loans/loans-response-model";
import {LoansService} from "../../services/loans.service";

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class profilComponent implements OnInit {
  user: CurrentUser | null = null;
  loans: LoansResponseModel[] = [];


  constructor(private authService: AuthService, private router: Router, private loansService: LoansService) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
    this.loansService.getMyLoans().subscribe((loans: LoansResponseModel[]) => {
      this.loans = loans;
    console.log(loans);
    })
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }


}