import { Pipe, PipeTransform } from '@angular/core';
import {ReservationStatus} from "../interfaces/reservation/response/reservation-status.model";

@Pipe({
    name: 'reservationStatus',
    standalone: true
})
export class ReservationStatusPipe implements PipeTransform {
    transform(value: ReservationStatus): string {
        switch (value) {
            case 'PENDING':
                return 'En attente';
            case 'READY_FOR_PICKUP':
                return 'Prêt à retirer';
            case 'FULFILLED':
                return 'Récupérée';
            case 'CANCELLED':
                return 'Annulée';
            case 'EXPIRED':
                return 'Expirée';
            default:
                return value;
        }
    }
}