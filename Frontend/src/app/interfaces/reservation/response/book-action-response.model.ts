// Ce que le back décide pour un livre donné et le lecteur connecté.
// La fiche livre ne calcule plus rien : elle affiche le bouton qui
// correspond à l'action reçue.
export type BookAction =
    | 'LOAN'      // un exemplaire est empruntable
    | 'PICKUP'    // réservation prête, exemplaire mis de côté
    | 'RESERVE'   // rien de libre, entrée en file possible
    | 'CANCEL'    // déjà dans la file
    | 'NONE';     // aucune action, reason explique pourquoi

export interface BookActionResponse {
    action: BookAction;
    reason: string | null;          // renseigné seulement pour NONE
    reservationId: number | null;   // renseigné pour CANCEL et PICKUP
    // Position réelle dans la file uniquement avec CANCEL.
    // Partout ailleurs le back envoie 0, qui veut dire « sans objet »
    // et non « premier ». Ne pas l'afficher hors CANCEL.
    rank: number;

    // Renseigné pour PICKUP seulement, et — attention — sans suffixe de
    // fuseau : Jackson sérialise LocalDateTime en « 2026-08-01T18:00:00 ».
    // JavaScript lit une telle chaîne comme heure LOCALE alors qu'elle est
    // en UTC : deux heures d'écart en France. Rétablir le « Z » avant tout
    // calcul ou affichage.
    pickupDeadline: string | null;
}