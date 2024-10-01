package site.mygumi.goodbite.domain.reservation.repository;

import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.entity.ReservationStatus;
import site.mygumi.goodbite.exception.reservation.ReservationErrorCode;
import site.mygumi.goodbite.exception.reservation.detail.ReservationNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findPageByCustomerId(Long customerId, Pageable pageable);

    Page<Reservation> findPageByRestaurantId(Long restaurantId, Pageable pageable);

    List<Reservation> findAllByRestaurantId(Long restaurantId);

    List<Reservation> findAllByStatus(ReservationStatus reservationStatus);

    List<Reservation> findAllByRestaurantIdAndDate(Long restaurantId, LocalDate date);

    default Reservation findByIdOrThrow(Long reservationId) {
        return findById(reservationId).orElseThrow(() -> new ReservationNotFoundException(
            ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    List<Reservation> findAllByCustomerId(Long id);
}