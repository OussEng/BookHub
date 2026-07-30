package fr.eni.bookhub.reservation.service;

import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.reservation.dao.IReservationDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationExpirationService {

    private final IReservationDao reservationDao;
    private final ReservationService reservationService;

    @Scheduled(fixedRate = 60 * 1000) // toutes les 15 minutes
    public void expireOverdueReservations() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        List<Reservation> expired = reservationDao
                .findByStatusAndPickupDeadlineBefore(ReservationStatus.READY_FOR_PICKUP, now);

        for (Reservation reservation : expired) {
            reservationService.expireOne(reservation.getId());
        }

        if (!expired.isEmpty()) {
            log.info("{} réservation(s) expirée(s)", expired.size());
        }
    }


}
