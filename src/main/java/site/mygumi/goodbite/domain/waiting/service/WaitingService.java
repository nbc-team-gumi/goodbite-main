package site.mygumi.goodbite.domain.waiting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.common.aspect.lock.RedisLock;
import site.mygumi.goodbite.domain.notification.controller.NotificationController;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import site.mygumi.goodbite.domain.waiting.dto.CreateWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.WaitingResponseDto;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import site.mygumi.goodbite.domain.waiting.exception.WaitingErrorCode;
import site.mygumi.goodbite.domain.waiting.exception.WaitingException;
import site.mygumi.goodbite.domain.waiting.exception.detail.WaitingNotFoundException;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;

/**
 * 대기 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 대기 생성, 조회, 수정, 삭제 등 대기 상태와 관련된 다양한 기능을 제공합니다.
 *
 * @author sillysillyman
 */
@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OwnerRepository ownerRepository;
    private final NotificationController notificationController;

    /**
     * 새로운 대기 요청을 생성합니다.
     *
     * @param createWaitingRequestDto 대기 요청 정보를 담은 DTO
     * @param user                    대기 요청을 생성하는 사용자의 인증 정보
     * @return 생성된 대기 정보를 담은 DTO
     */
    @RedisLock(key = "createWaitingLock")
    @Transactional
    public WaitingResponseDto createWaiting(CreateWaitingRequestDto createWaitingRequestDto,
        UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createWaitingRequestDto.getRestaurantId());

        Customer customer = customerRepository.findByIdOrThrow(user.getId());

        waitingRepository.validateByRestaurantIdAndCustomerId(restaurant.getId(),
            customer.getId());

        Long LastOrderNumber = findLastOrderNumber(restaurant.getId());

        Waiting waiting = new Waiting(
            restaurant,
            customer,
            LastOrderNumber + 1,
            WaitingStatus.WAITING, // 생성 시 무조건 Waiting
            createWaitingRequestDto.getPartySize(),
            createWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);

        String message = "새로운 웨이팅이 등록되었습니다.";
        notificationController.notifyOwner(restaurant.getId().toString(), message);

        return WaitingResponseDto.from(waiting);
    }

    /**
     * 특정 ID의 대기 정보를 조회합니다.
     *
     * @param waitingId 조회할 대기의 ID
     * @param user      대기 정보를 조회하는 사용자의 인증 정보
     * @return 조회된 대기 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public WaitingResponseDto getWaiting(Long waitingId, UserCredentials user) {

        validateWaitingRequest(user, waitingId);

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);
        return WaitingResponseDto.from(waiting);
    }

    /**
     * 특정 레스토랑의 모든 대기 순서를 하나씩 줄입니다.
     *
     * @param restaurantId 대기 순서를 줄일 레스토랑의 ID
     * @param user         요청하는 사용자의 인증 정보
     * @throws AuthException 사용자가 해당 작업에 권한이 없는 경우 발생합니다.
     */
    @Transactional
    public void reduceAllWaitingOrders(Long restaurantId, UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findByIdOrThrow(restaurant.getOwner().getId());

        if (!user.getEmail().equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        List<Waiting> waitingList = waitingRepository.findALLByRestaurantIdOrThrow(restaurantId);

        List<Waiting> waitingArrayList = new ArrayList<>();
        for (Waiting waiting : waitingList) {
            waiting.decrementWaitingOrder();
            if (waiting.getWaitingOrder() == 0) {

                //--------------
                // 알람 메서드 위치
                //--------------
                String message = "손님, 가게로 입장해 주세요.";
                notificationController.notifyCustomer(waiting.getId().toString(), message);
//                waitingRepository.delete(waiting);
                waiting.seat();
            } else {
                waitingArrayList.add(waiting);
            }

        }
        // 쿼리가 계속 나감...
        // 한꺼번에 범위로 줄일 수 있음
        waitingRepository.saveAll(waitingArrayList);
    }

    /**
     * 특정 대기 요청을 삭제하고 뒤의 대기 순서를 줄입니다.
     *
     * @param waitingId 삭제할 대기의 ID
     * @param user      요청하는 사용자의 인증 정보
     */
    @Transactional
    public void decrementWaitingOrder(Long waitingId, UserCredentials user) {

        validateWaitingRequest(user, waitingId);

        reduceWaitingOrders(waitingId, "reduce");
    }

    /**
     * 특정 대기 요청을 업데이트합니다.
     *
     * @param user                    요청하는 사용자의 인증 정보
     * @param waitingId               업데이트할 대기의 ID
     * @param updateWaitingRequestDto 대기 요청 업데이트 정보를 담은 DTO
     * @return 업데이트된 대기 정보를 담은 DTO
     */
    @Transactional
    public WaitingResponseDto updateWaiting(Long waitingId,
        UpdateWaitingRequestDto updateWaitingRequestDto, UserCredentials user) {

        validateWaitingRequest(user, waitingId);

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        waiting.update(updateWaitingRequestDto.getPartySize(), updateWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);
        return WaitingResponseDto.from(waiting);
    }

    /**
     * 특정 대기 요청을 취소합니다.
     *
     * @param user      요청하는 사용자의 인증 정보
     * @param waitingId 취소할 대기의 ID
     */
    @Transactional
    public void deleteWaiting(Long waitingId, UserCredentials user) {

        validateWaitingRequest(user, waitingId);

        reduceWaitingOrders(waitingId, "delete");
    }

    /**
     * 특정 레스토랑의 마지막 대기 순서 번호를 반환합니다.
     *
     * @param restaurantId 레스토랑의 ID
     * @return 마지막 대기 순서 번호
     */
    @Transactional(readOnly = true)
    public Long findLastOrderNumber(Long restaurantId) {
        if (!waitingRepository.findAllByRestaurantIdDeletedAtIsNull(restaurantId).isEmpty()) {
            // 해당하는 레스토랑에 예약이 하나라도 존재한다면
            return waitingRepository.findMaxWaitingOrderByRestaurantId(
                restaurantId);
        }
        // 해당하는 레스토랑에 예약이 하나도 없다
        return 0L;
    }

    /**
     * 특정 레스토랑의 대기 요청을 페이지네이션하여 조회합니다.
     *
     * @param user         요청하는 사용자의 인증 정보
     * @param restaurantId 레스토랑의 ID
     * @param pageable     페이지 정보
     * @return 페이지네이션된 대기 정보
     */
    @Transactional(readOnly = true)
    public Page<WaitingResponseDto> getWaitingsByRestaurantId(Long restaurantId,
        UserCredentials user, Pageable pageable) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findById(restaurant.getOwner().getId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));

        // api 요청한 유저가 해당 레스토랑의 '오너'와 같지 않다면
        if (!user.getEmail().equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Page<Waiting> waitingPage = waitingRepository.findPageByRestaurantId(restaurantId,
            pageable);

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(WaitingResponseDto::from).toList();
        return new PageImpl<>(waitingResponseDtos, pageable, waitingPage.getTotalElements());
    }

    /**
     * 사용자가 작성한 대기 요청을 페이지네이션하여 조회합니다.
     *
     * @param user     요청하는 사용자의 인증 정보
     * @param pageable 페이지 정보
     * @return 페이지네이션된 대기 정보
     */
    @Transactional(readOnly = true)
    public Page<WaitingResponseDto> getWaitings(UserCredentials user, Pageable pageable) {

        // 기존 pageable에 최신순 정렬을 추가
        Pageable sortedPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")  // 최신순으로 정렬
        );

        Page<Waiting> waitingPage = waitingRepository.findPageByCustomerId(user.getId(),
            sortedPageable);

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(WaitingResponseDto::from).toList();
        return new PageImpl<>(waitingResponseDtos, sortedPageable, waitingPage.getTotalElements());
    }

    /**
     * 특정 대기를 삭제하거나 순서를 감소시키고, 그 이후 대기의 순서를 조정합니다. 'delete' 타입일 경우 대기를 취소하고, 'reduce' 타입일 경우 대기를 완료
     * 처리합니다.
     *
     * @param waitingId 처리할 대기의 ID
     * @param type      'delete'는 대기 취소, 'reduce'는 대기 완료를 의미
     * @throws WaitingNotFoundException 지정된 ID의 대기가 존재하지 않을 경우 발생합니다.
     */
    private void reduceWaitingOrders(Long waitingId, String type) {
        Waiting waitingOne = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        List<Waiting> waitingList = waitingRepository.findAllByRestaurantIdDeletedAtIsNull(
            waitingOne.getRestaurant().getId());

        String message = "";
        boolean flag = false;
        WaitingStatus waitingStatus = WaitingStatus.WAITING;
        List<Waiting> waitingArrayList = new ArrayList<>();

        for (Waiting waiting : waitingList) {
            if (Objects.equals(waiting.getId(), waitingId)) {

                //--------------
                // 알람 메서드 위치
                // 현재 기능을 요청한 사람이 오너인지 손님인지 구분하는 메서드가 없음
                // 이후 구현을 요함
                //--------------

                if (type.equals("delete")) {
                    message = "웨이팅이 취소되었습니다.";
                    waitingStatus = WaitingStatus.CANCELLED;
                    notificationController.notifyOwner(waiting.getRestaurant().getId().toString(),
                        message);
                } else if (type.equals("reduce")) {
                    message = "손님, 가게로 입장해 주세요.";
                    waitingStatus = WaitingStatus.SEATED;
                }

                notificationController.notifyCustomer(waitingId.toString(), message);
                waiting.seat();

                flag = true;
            } else if (flag) {
                waiting.decrementWaitingOrder();
                waitingArrayList.add(waiting);
            }
        }
        if (!flag) {
            throw new WaitingNotFoundException(
                WaitingErrorCode.WAITING_NOT_FOUND);
        }
        // 쿼리 최적화
        waitingRepository.saveAll(waitingArrayList);
    }


    /**
     * 특정 대기에 대한 요청이 유효한지 확인합니다. 요청하는 사용자가 해당 대기의 손님이거나 해당 대기가 속한 레스토랑의 소유자인지 검증합니다.
     *
     * @param user      요청하는 사용자의 인증 정보
     * @param waitingId 검증할 대기의 ID
     * @throws AuthException     사용자가 해당 작업에 권한이 없는 경우 발생합니다.
     * @throws WaitingException  해당 레스토랑에 유효한 대기가 존재하지 않을 경우 발생합니다.
     * @throws CustomerException 대기 등록한 손님 정보가 존재하지 않을 경우 발생합니다.
     */
    private void validateWaitingRequest(UserCredentials user, Long waitingId) {

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            waiting.getRestaurant().getId());

        Customer customer = customerRepository.findById(waiting.getCustomer().getId())
            .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        Owner owner = ownerRepository.findById(restaurant.getOwner().getId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));

        // api 요청한 유저가 해당 레스토랑의 '오너'와 같던가 혹은 웨이팅 등록한 '손님'과 같던가
        if (user.getClass().equals(Owner.class) && !user.getEmail()
            .equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        if (user.getClass().equals(Customer.class) && !user.getEmail()
            .equals(customer.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        List<Waiting> waitingList = waitingRepository.findAllByRestaurantIdDeletedAtIsNull(
            restaurant.getId());
        if (waitingList.isEmpty()) {
            throw new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND);
        }
    }
}
