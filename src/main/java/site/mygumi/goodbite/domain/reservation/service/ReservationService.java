package site.mygumi.goodbite.domain.reservation.service;

import static site.mygumi.goodbite.domain.reservation.entity.Reservation.RESERVATION_DURATION_HOUR;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.domain.menu.entity.Menu;
import site.mygumi.goodbite.domain.menu.repository.MenuRepository;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import site.mygumi.goodbite.domain.reservation.dto.CreateReservationRequestDto;
import site.mygumi.goodbite.domain.reservation.dto.MenuItemRequestDto;
import site.mygumi.goodbite.domain.reservation.dto.ReservationResponseDto;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.entity.ReservationMenu;
import site.mygumi.goodbite.domain.reservation.entity.ReservationStatus;
import site.mygumi.goodbite.domain.reservation.exception.ReservationErrorCode;
import site.mygumi.goodbite.domain.reservation.exception.detail.DuplicateReservationException;
import site.mygumi.goodbite.domain.reservation.exception.detail.InvalidReservationTimeException;
import site.mygumi.goodbite.domain.reservation.exception.detail.MaxCapacityExceededException;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;

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

        validateReservationTime(createReservationRequestDto, restaurant);
        validateNoOverlappingReservation(createReservationRequestDto, user, restaurant);
        validateRestaurantCapacity(createReservationRequestDto, restaurant);

        List<MenuItemRequestDto> menuItems = createReservationRequestDto.getMenuItems();
        if (menuItems == null) {
            menuItems = new ArrayList<>(); // null이면 빈 리스트로 초기화
        }

        // ReservationMenu 엔티티 생성
        List<ReservationMenu> reservationMenus = createReservationMenus(menuItems);

        // Reservation 엔티티 생성
        Reservation reservation = createReservationRequestDto.toEntity(customer, restaurant,
            reservationMenus);

        // ReservationMenu에 Reservation 설정
        setReservationInMenus(reservationMenus, reservation);

        // 예약 확정
        reservation.confirm();
        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponseDto getReservation(Long reservationId, UserCredentials user) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);
        validateReservationOwnership(reservation, user);
        return ReservationResponseDto.from(reservation);
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponseDto> getMyReservations(UserCredentials user, Pageable pageable) {
        return reservationRepository.findPageByCustomerId(user.getId(), pageable)
            .map(ReservationResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponseDto> getAllReservationsByRestaurantId(Long restaurantId,
        UserCredentials user, Pageable pageable) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        validateRestaurantOwnership(restaurant, user);
        return reservationRepository.findPageByRestaurantId(restaurantId, pageable)
            .map(ReservationResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Integer getAvailableCapacity(Long restaurantId, LocalDate date, LocalTime time) {
        // 식당 정보 조회
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        // 운영 시간 확인
        OperatingHour operatingHour = operatingHourRepository.findByRestaurantIdAndDayOfWeekOrThrow(
            restaurant.getId(), date.getDayOfWeek());

        // 예약 종료 시간 계산
        LocalTime reservationEndTime = time.plusHours(
            RESERVATION_DURATION_HOUR); // time 오버플로우 발생 가능

        // 운영 시간 내에 있는지 확인
        if (time.isBefore(operatingHour.getOpenTime()) || reservationEndTime.isAfter(
            operatingHour.getCloseTime())) {
            throw new InvalidReservationTimeException(
                ReservationErrorCode.INVALID_RESERVATION_TIME);
        }

        // 주어진 시간의 예약 목록 조회
        List<Reservation> reservations = reservationRepository.findAllByRestaurantIdAndDate(
            restaurantId, date);

        // 예약된 총 인원 계산
        int reservedPartySize = reservations.stream()
            .filter(reservation -> {
                LocalTime existingStartTime = reservation.getTime();
                LocalTime existingEndTime = existingStartTime.plusHours(RESERVATION_DURATION_HOUR);
                // 시간대 겹침 확인
                return reservation.getStatus() == ReservationStatus.CONFIRMED &&
                    (time.isBefore(existingEndTime) && reservationEndTime.isAfter(
                        existingStartTime));
            })
            .mapToInt(Reservation::getPartySize)
            .sum();

        // 남아있는 수용 가능 인원 계산
        int availableCapacity = restaurant.getCapacity() - reservedPartySize;

        // 수용 가능 인원이 0보다 작을 수 없으므로, 최소 0으로 반환
        return Math.max(availableCapacity, 0);
    }

    @Transactional
    public void deleteReservation(Long reservationId, UserCredentials user) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);
        if (user.isCustomer()) {
            validateReservationOwnership(reservation, user);
            reservation.cancel();
        } else if (user.isOwner()) {
            validateRestaurantOwnership(reservation.getRestaurant(), user);
            reservation.reject();
        }
    }

    /**
     * 예약 시간이 식당의 영업 시간 내에 있는지 확인합니다. 예약 시간이 오픈 시간 이전이거나 마감 시간 이후일 경우,
     * InvalidReservationTimeException을 발생시킵니다.
     *
     * @param requestDto 예약 세부 사항이 담긴 CreateReservationRequestDto 객체
     * @param restaurant 예약하려는 식당 정보
     * @throws InvalidReservationTimeException 예약 시간이 영업 시간 내에 있지 않을 경우 발생
     */
    private void validateReservationTime(CreateReservationRequestDto requestDto,
        Restaurant restaurant) {
        OperatingHour operatingHour = operatingHourRepository.findByRestaurantIdAndDayOfWeekOrThrow(
            restaurant.getId(), requestDto.getDate().getDayOfWeek());
        LocalTime reservationTime = requestDto.getTime();
        LocalTime reservationEndTime = reservationTime.plusHours(RESERVATION_DURATION_HOUR);

        if (operatingHour.getCloseTime().isAfter(LocalTime.MIDNIGHT)) {
            if ((operatingHour.getCloseTime().isBefore(reservationTime) && reservationTime.isBefore(
                operatingHour.getOpenTime())) || reservationEndTime.isAfter(
                operatingHour.getOpenTime())) {
                throw new InvalidReservationTimeException(
                    ReservationErrorCode.INVALID_RESERVATION_TIME);
            }
        } else {
            if (reservationTime.isBefore(operatingHour.getOpenTime()) ||
                reservationEndTime.isAfter(operatingHour.getCloseTime())) {
                throw new InvalidReservationTimeException(
                    ReservationErrorCode.INVALID_RESERVATION_TIME);
            }
        }
    }

    /**
     * 동일한 사용자가 같은 날짜에 이미 겹치는 예약이 있는지 확인합니다. 만약 동일한 사용자가 겹치는 예약을 이미 가지고 있다면,
     * DuplicateReservationException을 발생시킵니다.
     *
     * @param requestDto 예약 세부 사항이 담긴 CreateReservationRequestDto 객체
     * @param user       예약을 시도하는 사용자의 UserCredentials 정보
     * @param restaurant 예약하려는 식당 정보
     * @throws DuplicateReservationException 동일한 시간대에 중복된 예약이 있을 경우 발생
     */
    private void validateNoOverlappingReservation(CreateReservationRequestDto requestDto,
        UserCredentials user, Restaurant restaurant) {

        List<Reservation> existingReservations = reservationRepository.findAllByRestaurantIdAndDate(
            restaurant.getId(), requestDto.getDate());

        boolean isTimeSlotOverlapping = existingReservations.stream().anyMatch(reservation -> {
            LocalTime existingStartTime = reservation.getTime();
            LocalTime existingEndTime = existingStartTime.plusHours(RESERVATION_DURATION_HOUR);
            return reservation.getStatus() == ReservationStatus.CONFIRMED &&
                reservation.getCustomer().getId().equals(user.getId()) && // 본인의 예약인지 확인
                (requestDto.getTime().isBefore(existingEndTime) &&
                    requestDto.getTime().plusHours(RESERVATION_DURATION_HOUR)
                        .isAfter(existingStartTime));
        });

        if (isTimeSlotOverlapping) {
            throw new DuplicateReservationException(ReservationErrorCode.DUPLICATE_RESERVATION);
        }
    }

    /**
     * 지정된 날짜에 예약된 전체 인원이 식당의 수용 인원을 초과하지 않는지 확인합니다. 총 예약 인원이 식당의 수용 인원을 초과할 경우, 예약이 거절되며
     * InvalidReservationTimeException을 발생시킵니다.
     *
     * @param requestDto 예약 세부 사항이 담긴 CreateReservationRequestDto 객체
     * @param restaurant 예약하려는 식당 정보
     * @throws MaxCapacityExceededException 예약 인원이 수용 인원을 초과할 경우 발생
     */
    private void validateRestaurantCapacity(CreateReservationRequestDto requestDto,
        Restaurant restaurant) {
        List<Reservation> existingReservations = reservationRepository.findAllByRestaurantIdAndDate(
            restaurant.getId(), requestDto.getDate());

        int currentReservedPartySize = existingReservations.stream().filter(reservation -> {
            LocalTime existingStartTime = reservation.getTime();
            LocalTime existingEndTime = existingStartTime.plusHours(RESERVATION_DURATION_HOUR);

            return (requestDto.getTime().isBefore(existingEndTime) &&
                requestDto.getTime().plusHours(RESERVATION_DURATION_HOUR)
                    .isAfter(existingStartTime)) &&
                reservation.getStatus() == ReservationStatus.CONFIRMED;
        }).mapToInt(Reservation::getPartySize).sum();

        int totalReservedPartySize = currentReservedPartySize + requestDto.getPartySize();

        if (totalReservedPartySize > restaurant.getCapacity()) {
//            Reservation rejectedReservation = requestDto.toEntity(
//                customerRepository.findByIdOrThrow(requestDto.getRestaurantId()), restaurant);
//            rejectedReservation.reject();
//            reservationRepository.save(rejectedReservation);
            throw new MaxCapacityExceededException(ReservationErrorCode.MAX_CAPACITY_EXCEEDED);
        }
    }

    /**
     * 주어진 예약이 현재 사용자와 관련된 예약인지 확인합니다. 사용자가 고객인 경우 예약이 해당 사용자 소유인지 확인하며, 사용자가 오너인 경우 예약이 사용자가 소유한
     * 식당의 예약인지 확인합니다. 검증에 실패할 경우 AuthException을 발생시킵니다.
     *
     * @param reservation 검증할 예약 정보
     * @param user        현재 사용자의 UserCredentials 정보
     * @throws AuthException 예약이 현재 사용자와 관련되지 않은 경우 발생
     */
    private void validateReservationOwnership(Reservation reservation, UserCredentials user) {
        if (user.isCustomer() && !reservation.getCustomer().getId().equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        if (user.isOwner() && !reservation.getRestaurant().getOwner().getId()
            .equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * 현재 사용자가 지정된 식당의 오너인지 확인합니다. 사용자가 해당 식당의 오너가 아닐 경우 AuthException을 발생시킵니다.
     *
     * @param restaurant 검증할 식당 정보
     * @param user       현재 사용자의 UserCredentials 정보
     * @throws AuthException 현재 사용자가 해당 식당의 오너가 아닌 경우 발생
     */
    private void validateRestaurantOwnership(Restaurant restaurant, UserCredentials user) {
        if (!restaurant.getOwner().getId().equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * 주어진 MenuItemDto 리스트로부터 ReservationMenu 엔티티 리스트를 생성합니다. 각 ReservationMenu 엔티티는 MenuItemDto에서
     * 제공된 메뉴 ID와 수량을 기반으로 생성됩니다.
     *
     * @param menuItems 메뉴 ID와 수량이 담긴 MenuItemDto 리스트
     * @return ReservationMenu 엔티티 리스트
     */
    private List<ReservationMenu> createReservationMenus(List<MenuItemRequestDto> menuItems) {

        return menuItems.stream().map(menuItemDto -> {
            Long menuId = menuItemDto.getMenuId();
            int quantity = menuItemDto.getQuantity();
            Menu menu = menuRepository.findByIdOrThrow(menuId);
            return ReservationMenu.builder()
                .menu(menu)
                .quantity(quantity)
                .build();
        }).toList();
    }

    /**
     * 제공된 ReservationMenu 엔티티들에 Reservation을 설정합니다. 이 메서드는 Reservation과 ReservationMenu 엔티티 간의 관계를
     * 설정하는 데 사용됩니다.
     *
     * @param reservationMenus 업데이트할 ReservationMenu 엔티티 리스트
     * @param reservation      각 ReservationMenu에 설정할 Reservation 엔티티
     */
    private void setReservationInMenus(List<ReservationMenu> reservationMenus,
        Reservation reservation) {
        for (ReservationMenu reservationMenu : reservationMenus) {
            reservationMenu.setReservation(reservation);
        }
    }
}