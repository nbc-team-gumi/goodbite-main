package site.mygumi.goodbite.domain.reservation.service;

import static site.mygumi.goodbite.domain.reservation.entity.Reservation.RESERVATION_DURATION_HOUR;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.entity.ReservationStatus;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;

@RequiredArgsConstructor
@Service
public class ReservationStatusSchedulingService {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 상태를 완료 상태로 갱신하는 스케줄링 메서드입니다. 예약된 시간으로부터 일정 시간이 지난 예약의 상태를 `COMPLETED`로 변경합니다. 이 메서드는 매시간
     * 0분과 30분에 실행됩니다. 예약이 CONFIRMED 상태이며 예약 시간으로부터 `RESERVATION_DURATION_HOUR` 시간이 지났다면 예약 상태를
     * COMPLETED로 갱신합니다. 매 정시와 30분에 (예: 00:00, 00:30, 01:00, 01:30 등) 실행됩니다.
     */
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