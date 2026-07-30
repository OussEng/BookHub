export type BookAction = 'LOAN' | 'RESERVE' | 'CANCEL' | 'PICKUP' | 'NONE';

export interface BookActionResponse {
    action: BookAction;
    reason: string | null;
    reservationId: number | null;
    rank: number;
    pickupDeadline: string | null;
}