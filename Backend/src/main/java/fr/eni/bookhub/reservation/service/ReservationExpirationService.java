package fr.eni.bookhub.reservation.service;

import fr.eni.bookhub.reservation.dao.IReservationDao;
import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationExpirationService {

    private final IReservationDao reservationDao;
    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 60 * 1000) // 1 min en test, 15 min en prod
    public void expireOverdueReservations() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        List<Reservation> expired = reservationDao
                .findByStatusAndPickupDeadlineBefore(ReservationStatus.READY_FOR_PICKUP, now);

        int ok = 0;
        for (Reservation reservation : expired) {
            try {
                reservationService.expireOne(reservation.getId());
                ok++;
            } catch (Exception e) {
                log.error("Expiration impossible pour la réservation {}", reservation.getId(), e);
            }
        }

        if (!expired.isEmpty()) {
            log.info("{}/{} réservation(s) expirée(s)", ok, expired.size());
        }
    }


}
