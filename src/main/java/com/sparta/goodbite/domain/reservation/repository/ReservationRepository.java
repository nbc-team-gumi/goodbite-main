package com.sparta.goodbite.domain.reservation.repository;

import com.sparta.goodbite.domain.reservation.entity.Reservation;
import com.sparta.goodbite.domain.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByCustomerId(Long customerId);

    List<Reservation> findAllByRestaurantId(Long restaurantId);

    List<Reservation> findAllByStatus(ReservationStatus reservationStatus);

    List<Reservation> findAllByRestaurantIdAndDate(Long restaurantId, LocalDate date);

    default Reservation findByIdOrThrow(Long reservationId) {
        return findById(reservationId).orElseThrow(() -> new RuntimeException("임시 메시지"));
    }
}