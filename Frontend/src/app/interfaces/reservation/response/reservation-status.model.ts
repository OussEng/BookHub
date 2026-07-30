export type ReservationStatus =
    | 'PENDING'   // En file d'attente
    | 'READY_FOR_PICKUP' // Exemplaire mis de côté, 72h pour cliquer
    | 'FULFILLED' // Emprunté : la réservation devient un emprunt
    | 'CANCELLED' // Annulée avant le retrait
    | 'EXPIRED';  // Délai de 72h écoulé