package fr.eni.bookhub.reservation.entity;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

// Statuts possibles d'une réservation, du dépôt de la demande à sa clôture
public enum ReservationStatus {

    PENDING, // En file d'attente
    READY_FOR_PICKUP, // Exemplaire mis de côté, 72h pour cliquer
    FULFILLED, // L'utilisateur a cliqué sur "emprunter".
    CANCELLED, // Annulée avant le retrait
    EXPIRED; // Délai de 72h écoulé

    // Réservations encore en vie : elles comptent dans les plafonds et interdisent le doublon
    public static final Set<ReservationStatus> ACTIFS =
            Collections.unmodifiableSet(EnumSet.of(PENDING, READY_FOR_PICKUP));
}
