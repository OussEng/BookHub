package fr.eni.bookhub.reservation.entity;

// Statuts possibles d'une réservation, du dépôt de la demande à sa clôture
public enum ReservationStatus {

    PENDING, // En file d'attente
    AVAILABLE, // Exemplaire mis de côté, 24h pour cliquer
    FULFILLED, // Emprunté : la réservation devient un emprunt
    CANCELLED, // Annulée avant le retrait
    EXPIRED // Délai de 24h écoulé

}
