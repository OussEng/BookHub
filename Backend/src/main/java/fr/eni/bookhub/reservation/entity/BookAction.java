package fr.eni.bookhub.reservation.entity;

public enum BookAction {
    LOAN,       // exemplaire disponible : emprunt direct
    PICKUP,     // réservation prête : retrait de l'exemplaire mis de côté
    RESERVE,    // rien de disponible : entrée en file
    CANCEL,     // déjà en file : annulation possible
    NONE        // aucune action, `reason` explique pourquoi
}