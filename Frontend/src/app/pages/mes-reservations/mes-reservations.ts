import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReservationService } from '../../services/reservation.service';
import { ReservationResponse } from '../../interfaces/reservation/response/reservation-response.model';
import { ReservationStatus } from '../../interfaces/reservation/response/reservation-status.model';

@Component({
    selector: 'app-mes-reservations',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './mes-reservations.html',
    styleUrl: './mes-reservations.css',
})
export class MesReservations implements OnInit {

    private reservationService = inject(ReservationService);

    reservations: ReservationResponse[] = [];
    protected isLoading = signal<boolean>(true);
    errorMessage = '';

    ngOnInit() {
        this.reservationService.mesReservations().subscribe({
            next: (data) => {
                this.reservations = data;
                this.isLoading.set(false);
            },
            error: (err) => {
                this.errorMessage = "Impossible de charger vos réservations.";
                this.isLoading.set(false);
                console.error(err);
            }
        });
    }

    // Le back renvoie les statuts bruts de l'énumération.
    // Le front ne fait que les traduire : aucune règle métier ici.
    libelleStatut(statut: ReservationStatus): string {
        switch (statut) {
            case 'PENDING':   return "En attente";
            case 'AVAILABLE': return "Disponible";
            case 'FULFILLED': return "Empruntée";
            case 'CANCELLED': return "Annulée";
            case 'EXPIRED':   return "Expirée";
            default:          return statut;
        }
    }

    // Deux messages seulement, et ils ne disent pas la même chose :
    // AVAILABLE presse le lecteur, FULFILLED le rassure.
    consigne(reservation: ReservationResponse): string {
        if (reservation.status === 'AVAILABLE') {
            return "Cliquez sur Prendre avant l'échéance, sinon l'exemplaire passe au suivant.";
        }
        if (reservation.status === 'FULFILLED') {
            return "Le livre est à vous, passez le chercher aux heures d'ouverture.";
        }
        return '';
    }

    // Le rang n'a de sens que dans la file : une réservation empruntée,
    // annulée ou expirée n'y est plus.
    rangAffichable(statut: ReservationStatus): boolean {
        return statut === 'PENDING' || statut === 'AVAILABLE';
    }

    // Classe CSS par statut, pour la pastille de couleur
    classeStatut(statut: ReservationStatus): string {
        return 'statut statut--' + statut.toLowerCase();
    }
}
