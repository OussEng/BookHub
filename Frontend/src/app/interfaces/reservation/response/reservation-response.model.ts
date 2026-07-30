import {ReservationStatus} from "./reservation-status.model";

export interface ReservationResponse {
    id: number;
    bookId: number;
    bookTitle: string;
    reservesDate: string;
    rank: number;
    status: ReservationStatus;
    pickupDeadline: string | null;
    canBeCancelled: boolean;
}