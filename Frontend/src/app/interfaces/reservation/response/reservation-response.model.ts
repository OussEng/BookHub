import { ReservationStatus } from './reservation-status.model';

export interface ReservationResponse {
    id: number;
    bookId: number;
    reservesDate: string;
    rank: number;
    status: ReservationStatus;
    pickupDeadline: string | null;
    bookTitle: string;
    canBeCancelled: boolean;
}