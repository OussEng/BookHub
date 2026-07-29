import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
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
export class MesReservations implements OnInit, OnDestroy {

    private reservationService = inject(ReservationService);

    reservations: ReservationResponse[] = [];
    protected isLoading = signal<boolean>(true);
    errorMessage = '';

    // Sert uniquement à forcer le rafraîchissement de l'affichage :
    // le décompte est recalculé à chaque cycle de détection.
    protected maintenant = signal<number>(Date.now());
    private minuteur?: ReturnType<typeof setInterval>;

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

        // Une minute suffit : la fenêtre est de 72h, afficher la seconde
        // n'apporterait rien et ferait travailler le navigateur pour rien.
        this.minuteur = setInterval(() => this.maintenant.set(Date.now()), 60_000);
    }

    ngOnDestroy() {
        // Sans cet arrêt, le minuteur survit à la page et fuit.
        if (this.minuteur) {
            clearInterval(this.minuteur);
        }
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

    // Le décompte n'a de sens que pendant les 72h, donc au statut AVAILABLE.
    decompteAffichable(reservation: ReservationResponse): boolean {
        return reservation.status === 'AVAILABLE' && !!reservation.pickupDeadline;
    }

    // pickupDeadline arrive en UTC. new Date() lit la chaîne ISO et
    // ramène tout en instant absolu : la comparaison est juste quel que
    // soit le fuseau du lecteur. Aucune conversion à faire à la main.
    tempsRestant(reservation: ReservationResponse): string {
        if (!reservation.pickupDeadline) {
            return '—';
        }

        const echeance = new Date(reservation.pickupDeadline).getTime();
        const reste = echeance - this.maintenant();

        if (reste <= 0) {
            return "délai écoulé";
        }

        const minutes = Math.floor(reste / 60_000);
        const heures  = Math.floor(minutes / 60);
        const jours   = Math.floor(heures / 24);

        if (jours > 0)   return `${jours} j ${heures % 24} h`;
        if (heures > 0)  return `${heures} h ${minutes % 60} min`;
        return `${minutes} min`;
    }

    // Moins de 6h : le lecteur doit se dépêcher, on le signale en couleur.
    decompteUrgent(reservation: ReservationResponse): boolean {
        if (!reservation.pickupDeadline) {
            return false;
        }
        const reste = new Date(reservation.pickupDeadline).getTime() - this.maintenant();
        return reste > 0 && reste < 6 * 3_600_000;
    }
}