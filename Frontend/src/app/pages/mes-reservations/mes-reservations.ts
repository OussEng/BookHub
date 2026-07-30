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

    protected reservations = signal<ReservationResponse[]>([]);
    protected isLoading = signal<boolean>(true);
    protected errorMessage = signal<string>('');

    // Sert uniquement à forcer le rafraîchissement de l'affichage :
    // le décompte est recalculé à chaque cycle de détection.
    protected maintenant = signal<number>(Date.now());
    private minuteur?: ReturnType<typeof setInterval>;

    // Identifiant de la réservation en cours d'annulation : le bouton
    // correspondant est désactivé le temps de l'aller-retour.
    protected enCoursAnnulation = signal<number | null>(null);
    // Même principe pour le retrait : le bouton « Prendre » se désactive
    // le temps de l'aller-retour.
    protected enCoursRetrait = signal<number | null>(null);

    ngOnInit() {
        this.charger();

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

    // Sortie de secours si le premier chargement a échoué : sans elle,
    // l'écran reste bloqué sur le message d'erreur, puisque le tableau
    // — et donc les boutons — n'est pas rendu tant qu'il est affiché.
    reessayer() {
        this.isLoading.set(true);
        this.errorMessage.set('');
        this.charger();
    }

    private charger() {
        this.reservationService.mesReservations().subscribe({
            next: (data) => {
                this.reservations.set(data);
                this.errorMessage.set('');
                this.isLoading.set(false);
            },
            error: (err) => {
                this.errorMessage.set("Impossible de charger vos réservations.");
                this.isLoading.set(false);
                console.error(err);
            }
        });
    }

    prendre(reservation: ReservationResponse) {
        this.enCoursRetrait.set(reservation.id);

        this.reservationService.prendre(reservation.bookId).subscribe({
            next: () => {
                this.enCoursRetrait.set(null);
                // La réservation devient FULFILLED et un prêt est créé :
                // seul le serveur connaît le nouvel état de la liste.
                this.charger();
            },
            error: (err) => {
                this.enCoursRetrait.set(null);
                console.error(err);
            }
        });
    }

    annuler(reservation: ReservationResponse) {
        // Geste irréversible : une réservation annulée ne revient pas dans
        // la file, il faudrait en créer une nouvelle.
        const message = reservation.status === 'READY_FOR_PICKUP'
            ? "L'exemplaire est mis de côté pour vous. En annulant, il passe au lecteur suivant. Confirmer ?"
            : "Annuler cette réservation ? Vous perdrez votre place dans la file.";

        if (!confirm(message)) {
            return;
        }

        this.enCoursAnnulation.set(reservation.id);

        this.reservationService.annuler(reservation.id).subscribe({
            next: () => {
                this.enCoursAnnulation.set(null);
                // Rechargement complet et non retrait local : l'annulation
                // décale le rang de tous les suivants, et peut avoir promu
                // quelqu'un. Seul le serveur connaît le nouvel état.
                this.charger();
            },
            error: (err) => {
                this.enCoursAnnulation.set(null);
                console.error(err);
            }
        });
    }

    // Le back renvoie les statuts bruts de l'énumération.
    // Le front ne fait que les traduire : aucune règle métier ici.
    libelleStatut(statut: ReservationStatus): string {
        switch (statut) {
            case 'PENDING':   return "En attente";
            case 'READY_FOR_PICKUP': return "Disponible";
            case 'FULFILLED': return "Empruntée";
            case 'CANCELLED': return "Annulée";
            case 'EXPIRED':   return "Expirée";
            default:          return statut;
        }
    }

    // Deux messages seulement, et ils ne disent pas la même chose :
    // READY_FOR_PICKUP presse le lecteur, FULFILLED le rassure.
    consigne(reservation: ReservationResponse): string {
        if (reservation.status === 'READY_FOR_PICKUP') {
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
        return statut === 'PENDING' || statut === 'READY_FOR_PICKUP';
    }

    // Classe CSS par statut, pour la pastille de couleur
    classeStatut(statut: ReservationStatus): string {
        return 'statut statut--' + statut.toLowerCase();
    }

    // Le décompte n'a de sens que pendant les 72h, donc au statut READY_FOR_PICKUP.
    decompteAffichable(reservation: ReservationResponse): boolean {
        return reservation.status === 'READY_FOR_PICKUP' && !!reservation.pickupDeadline;
    }

    // Jackson sérialise LocalDateTime sans suffixe de fuseau. Une chaîne
    // sans « Z » est lue comme heure LOCALE par JavaScript, alors que le
    // back l'écrit en UTC : deux heures d'écart en France. On rétablit
    // l'information manquante avant de convertir.
    private enInstant(iso: string): number {
        return new Date(iso.endsWith('Z') ? iso : iso + 'Z').getTime();
    }

    // Même correction pour l'affichage : la pipe date reçoit un objet Date
    // construit à partir de l'instant réel, et non la chaîne brute qu'elle
    // interpréterait comme heure locale.
    enDate(iso: string | null): Date | null {
        return iso ? new Date(this.enInstant(iso)) : null;
    }

    // pickupDeadline arrive en UTC mais sans suffixe de fuseau :
    // enInstant rétablit le « Z » avant la comparaison.
    tempsRestant(reservation: ReservationResponse): string {
        if (!reservation.pickupDeadline) {
            return '—';
        }

        const echeance = this.enInstant(reservation.pickupDeadline);
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
        const reste = this.enInstant(reservation.pickupDeadline) - this.maintenant();
        return reste > 0 && reste < 6 * 3_600_000;
    }
}