import {ReservationStatus} from "./reservation-status.model";

export interface ReservationResponse {
    id: number;
    bookId: number;
    bookTitle: string;
    reservesDate: string;
    rank: number;
    status: ReservationStatus;

    // Position réelle dans la file uniquement avec CANCEL.
    // Renseigné pour READY_FOR_PICKUP seulement, et sans suffixe de
    // fuseau : Jackson sérialise LocalDateTime en « 2026-08-01T18:00:00 ».
    // JavaScript lit une telle chaîne comme heure LOCALE alors qu'elle
    // est en UTC : deux heures d'écart en France.
    pickupDeadline: string | null;

    canBeCancelled: boolean;
}
