import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CurrentUser } from '../../interfaces/current user/current-user';
import { LoansResponseModel } from "../../interfaces/loans/loans-response-model";
import { LoansService } from "../../services/loans.service";
import { LoanStatusPipe } from "../../pipes/loan-status-pipe";
import { ReservationResponse } from "../../interfaces/reservation/response/reservation-response.model";
import { ReservationStatusPipe } from "../../pipes/reservation-status-pipe";
import { FlashMessageService } from "../../services/flash-message-service/flash-message-service";
import {ReservationService} from "../../services/reservation.service";

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, LoanStatusPipe, ReservationStatusPipe],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class profilComponent implements OnInit {
  user: CurrentUser | null = null;
  loans: LoansResponseModel[] = [];
  reservations: ReservationResponse[] = [];

  protected isLoading = signal<boolean>(true);
  protected isReservationsLoading = signal<boolean>(true);
  protected isCancelling = signal<number | null>(null);

  constructor(
      private authService: AuthService,
      private router: Router,
      private loansService: LoansService,
      private reservationService: ReservationService,
      private flashService: FlashMessageService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();

    this.loansService.getMyLoans().subscribe((loans: LoansResponseModel[]) => {
      this.loans = loans.reverse();
      this.isLoading.set(false);
    });

    this.loadReservations();
  }

  loadReservations(): void {
    this.isReservationsLoading.set(true);
    this.reservationService.mesReservations().subscribe({
      next: (reservations) => {
        this.reservations = reservations;
        this.isReservationsLoading.set(false);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des réservations', err);
        this.isReservationsLoading.set(false);
      }
    });
  }

  cancelReservation(reservationId: number): void {
    this.isCancelling.set(reservationId);

    this.reservationService.annuler(reservationId).subscribe({
      next: () => {
        this.flashService.success("Réservation annulée avec succès");
        this.loadReservations();
        this.isCancelling.set(null);
      },
      error: (err) => {
        this.flashService.error("Impossible d'annuler la réservation");
        console.error(err);
        this.isCancelling.set(null);
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}