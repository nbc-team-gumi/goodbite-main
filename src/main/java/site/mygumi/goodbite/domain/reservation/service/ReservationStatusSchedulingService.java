package site.mygumi.goodbite.domain.reservation.service;

import static site.mygumi.goodbite.domain.reservation.entity.Reservation.RESERVATION_DURATION_HOUR;

import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.entity.ReservationStatus;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationStatusSchedulingService {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 0,30 * * * ?") // 0초, 0분 || 30분, 모든 시간, 모든 날, 모든 달, 요일 상관 없이
    @Transactional
    public void completeReservationStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findAllByStatus(
            ReservationStatus.CONFIRMED);

        for (Reservation reservation : reservations) {
            // 식당 이용 종료 시간이 현재 시간보다 이전이면 상태를 COMPLETED로 변경
            if (LocalDateTime.of(reservation.getDate(),
                    reservation.getTime()).plusHours(RESERVATION_DURATION_HOUR)
                .isBefore(now)) {
                reservation.complete();
            }
        }
    }
}