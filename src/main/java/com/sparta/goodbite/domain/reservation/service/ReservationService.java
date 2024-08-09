package com.sparta.goodbite.domain.reservation.service;

import static com.sparta.goodbite.domain.reservation.entity.Reservation.RESERVATION_DURATION_HOUR;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.customer.repository.CustomerRepository;
import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.menu.repository.MenuRepository;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.reservation.dto.CreateReservationRequestDto;
import com.sparta.goodbite.domain.reservation.dto.ReservationResponseDto;
import com.sparta.goodbite.domain.reservation.entity.Reservation;
import com.sparta.goodbite.domain.reservation.entity.ReservationMenu;
import com.sparta.goodbite.domain.reservation.entity.ReservationStatus;
import com.sparta.goodbite.domain.reservation.repository.ReservationRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final CustomerRepository customerRepository;
    private final MenuRepository menuRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final OperatingHourRepository operatingHourRepository;

    @Transactional
    public void createReservation(CreateReservationRequestDto createReservationRequestDto,
        UserCredentials user) {
        Customer customer = customerRepository.findByIdOrThrow(user.getId());
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createReservationRequestDto.getRestaurantId());
        OperatingHour operatingHour = operatingHourRepository.findByRestaurantIdAndDayOfWeekOrThrow(
            restaurant.getId(), createReservationRequestDto.getDate().getDayOfWeek());
        LocalTime reservationTime = createReservationRequestDto.getTime();
        LocalTime reservationEndTime = reservationTime.plusHours(RESERVATION_DURATION_HOUR);

        if (reservationTime.isBefore(operatingHour.getOpenTime()) ||
            reservationEndTime.isAfter(operatingHour.getCloseTime())) {
            throw new IllegalArgumentException("예약 시간이 영업 시간 내에 있지 않습니다.");
        }

        // 예약 시간대의 현재 예약 수 확인 (식당 이용 시간 고려)
        List<Reservation> existingReservations = reservationRepository.findAllByRestaurantIdAndDate(
            restaurant.getId(), createReservationRequestDto.getDate());

        int currentReservedPartySize = existingReservations.stream().filter(reservation -> {
            LocalTime existingStartTime = reservation.getTime();
            LocalTime existingEndTime = existingStartTime.plusHours(RESERVATION_DURATION_HOUR);

            return (reservationTime.isBefore(existingEndTime) && reservationEndTime.isAfter(
                existingStartTime)) && reservation.getStatus() == ReservationStatus.CONFIRMED;
        }).mapToInt(Reservation::getPartySize).sum();
        int totalReservedPartySize =
            currentReservedPartySize + createReservationRequestDto.getPartySize();

        if (totalReservedPartySize > restaurant.getCapacity()) {
            // 예약 거절: REJECTED 상태로 저장
            Reservation rejectedReservation = createReservationRequestDto.toEntity(customer,
                restaurant, null);
            rejectedReservation.reject();
            reservationRepository.save(rejectedReservation);
            return; // 예약 절차 종료
        }

        // ReservationMenu 엔티티 생성
        List<ReservationMenu> reservationMenus = createReservationRequestDto.getMenuQuantities()
            .entrySet().stream()
            .map(entry -> {
                Long menuId = entry.getKey();
                Integer quantity = entry.getValue();
                Menu menu = menuRepository.findByIdOrThrow(menuId);
                return ReservationMenu.builder()
                    .menu(menu)
                    .quantity(quantity)
                    .reservation(null) // 나중에 Reservation에 추가할 때 설정
                    .build();
            }).toList();

        // Reservation 엔티티 생성
        Reservation reservation = createReservationRequestDto.toEntity(customer, restaurant,
            reservationMenus);

        // ReservationMenu에 Reservation 설정
        for (ReservationMenu reservationMenu : reservationMenus) {
            reservationMenu.setReservation(reservation);
        }

        // 예약 확정
        reservation.confirm();
        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponseDto getReservation(Long reservationId, UserCredentials user) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);

        // 고객일 경우, 예약이 현재 사용자의 예약인지 확인
        if (user.isCustomer() && !reservation.getCustomer().getId().equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        // 오너일 경우, 예약이 현재 사용자가 소유한 레스토랑의 예약인지 확인
        if (user.isOwner() && !reservation.getRestaurant().getOwner().getId()
            .equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return ReservationResponseDto.from(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getMyReservations(UserCredentials user) {
        return reservationRepository.findAllByCustomerId(user.getId()).stream()
            .map(ReservationResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getAllReservationsByRestaurantId(Long restaurantId,
        UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        // 현재 사용자가 해당 레스토랑의 오너인지 확인
        if (!restaurant.getOwner().getId().equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        // 해당 레스토랑의 모든 예약 조회
        List<Reservation> reservations = reservationRepository.findAllByRestaurantId(restaurantId);

        return reservations.stream()
            .map(ReservationResponseDto::from)
            .toList();
    }

    @Transactional
    public void deleteReservation(Long reservationId, UserCredentials user) {
        // 예약 ID로 예약 정보 가져오기
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);

        // 예약이 현재 사용자와 연관된 예약인지 확인
        if (!reservation.getCustomer().getId().equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        // 예약 삭제
        reservation.delete();
    }
}