package fr.eni.bookhub.reservation.controller;

import fr.eni.bookhub.reservation.dto.response.BookActionResponse;
import fr.eni.bookhub.reservation.dto.response.ReservationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * OpenAPI documentation interface for {@link ReservationController}.
 *
 * @see ReservationController
 */
@Tag(
        name = "Reservations",
        description = "Reservation queue management for books with no available copy. Enforces a first-come-first-served queue with a 72-hour pickup window once a copy is set aside."
)
@SecurityRequirement(name = "bearerAuth")
public interface ReservationControllerApi {

    @Operation(
            summary = "Reserve a book",
            description = """
                    Places the authenticated user in the reservation queue for a book.
                    
                    Rejected if the book is currently loaned or reserved by the same user already, if the user has reached the limit of 5 active reservations, or if a copy of the book is actually available for direct loan (reservation is only for unavailable books).
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Reservation successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 5,
                                                      "bookId": 1,
                                                      "bookTitle": "1984",
                                                      "reservesDate": "2026-07-30T10:00:00",
                                                      "rank": 2,
                                                      "status": "PENDING",
                                                      "pickupDeadline": null,
                                                      "canBeCancelled": true
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Ouvrage introuvable\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Reservation not possible",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Already loaned by user", value = "{\"error\": \"Vous empruntez déjà ce livre\"}"),
                                            @ExampleObject(name = "Already reserved by user", value = "{\"error\": \"Vous réservez déjà ce livre\"}"),
                                            @ExampleObject(name = "Reservation limit reached", value = "{\"error\": \"Limite de 5 réservations en cours atteinte\"}"),
                                            @ExampleObject(name = "Book actually available", value = "{\"error\": \"Le livre est disponible, empruntez-le directement\"}")
                                    }
                            )
                    )
            }
    )
    ResponseEntity<ReservationResponse> createReservation(@PathVariable Long bookId);

    @Operation(
            summary = "Cancel a reservation",
            description = "Cancels one of the authenticated user's own active reservations (PENDING or READY_FOR_PICKUP). If the cancelled reservation had a copy set aside, the next reservation in the queue is promoted automatically.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Reservation successfully cancelled", content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = "The reservation does not belong to the current user",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Vous n'êtes pas autorisé à annuler cette réservation\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Reservation not found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Réservation introuvable\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Reservation is not in an active state",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Cette réservation n'est pas active et ne peut pas être annulée\"}"))
                    )
            }
    )
    ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId);

    @Operation(
            summary = "List my reservations",
            description = "Returns all reservations of the authenticated user, including closed ones (cancelled, expired, fulfilled), each with its current queue rank (0 if not active).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reservations successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": 5,
                                                        "bookId": 1,
                                                        "bookTitle": "1984",
                                                        "reservesDate": "2026-07-30T10:00:00",
                                                        "rank": 2,
                                                        "status": "PENDING",
                                                        "pickupDeadline": null,
                                                        "canBeCancelled": true
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<ReservationResponse>> getMyReservations();

    @Operation(
            summary = "Get the available action for a book",
            description = """
                    Computes, for the authenticated user and a given book, the single relevant action to offer on the client: PICKUP (a reservation is ready and within its pickup window), CANCEL (an active reservation can be cancelled), LOAN (a copy is directly available), RESERVE (nothing available, join the queue), or NONE (no action possible, with `reason` explaining why — already borrowed, loan limit, or reservation limit reached).
                    
                    Note: `bookId` existence is not checked here; an unknown book ID falls through to the default RESERVE action rather than raising an error.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Action successfully computed",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Loan available", value = "{\"action\": \"LOAN\", \"reason\": null, \"reservationId\": null, \"rank\": 0, \"pickupDeadline\": null}"),
                                            @ExampleObject(name = "Reservation ready for pickup", value = "{\"action\": \"PICKUP\", \"reason\": null, \"reservationId\": 5, \"rank\": 0, \"pickupDeadline\": \"2026-08-02T10:00:00\"}"),
                                            @ExampleObject(name = "No action, limit reached", value = "{\"action\": \"NONE\", \"reason\": \"Limite de 3 emprunts atteinte\", \"reservationId\": null, \"rank\": 0, \"pickupDeadline\": null}")
                                    }
                            )
                    )
            }
    )
    ResponseEntity<BookActionResponse> getAvailableAction(@PathVariable Long bookId);
}