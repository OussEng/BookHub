package fr.eni.bookhub.reservation.entity;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

// Statuts possibles d'une réservation, du dépôt de la demande à sa clôture
public enum ReservationStatus {

    PENDING, // En file d'attente
    AVAILABLE, // Exemplaire mis de côté, 72h pour cliquer
    FULFILLED, // Emprunté : la réservation devient un emprunt
    CANCELLED, // Annulée avant le retrait
    EXPIRED; // Délai de 72h écoulé

    // Réservations encore en vie : elles comptent dans les plafonds et interdisent le doublon
    public static final Set<ReservationStatus> ACTIFS =
            Collections.unmodifiableSet(EnumSet.of(PENDING, AVAILABLE));
}
