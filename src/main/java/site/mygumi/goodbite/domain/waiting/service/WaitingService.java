package site.mygumi.goodbite.domain.waiting.service;

import java.time.LocalDateTime;
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
import site.mygumi.goodbite.aspect.lock.RedisLock;
import site.mygumi.goodbite.domain.notification.controller.NotificationController;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import site.mygumi.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.WaitingResponseDto;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;
import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.CustomerException;
import site.mygumi.goodbite.exception.waiting.WaitingErrorCode;
import site.mygumi.goodbite.exception.waiting.WaitingException;
import site.mygumi.goodbite.exception.waiting.detail.WaitingNotFoundException;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OwnerRepository ownerRepository;
    private final NotificationController notificationController;

    @RedisLock(key = "createWaitingLock")
    @Transactional
    public WaitingResponseDto createWaiting(UserCredentials user,
        PostWaitingRequestDto postWaitingRequestDto) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            postWaitingRequestDto.getRestaurantId());

        Customer customer = customerRepository.findByIdOrThrow(user.getId());

        waitingRepository.validateByRestaurantIdAndCustomerId(restaurant.getId(),
            customer.getId());

        Long LastOrderNumber = findLastOrderNumber(restaurant.getId());

        Waiting waiting = new Waiting(
            restaurant,
            customer,
            LastOrderNumber + 1,
            WaitingStatus.WAITING, // 생성 시 무조건 Waiting
            postWaitingRequestDto.getPartySize(),
            postWaitingRequestDto.getWaitingType(),
            postWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);

        String message = "새로운 웨이팅이 등록되었습니다.";
        notificationController.notifyOwner(restaurant.getId().toString(), message);

        return WaitingResponseDto.of(waiting);
    }

    // 단일 조회용 메서드
    @Transactional(readOnly = true)
    public WaitingResponseDto getWaiting(UserCredentials user, Long waitingId) {

        validateWaitingRequest(user, waitingId);

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);
        return WaitingResponseDto.of(waiting);
    }

    // 가게 주인용 api
    // 해당 메서드 동작 시, 가게의 id가 들어간 orders가 하나씩 줄게 된다.
    // restaurant id에 맞는 Waiting들의 order를 하나씩 줄인다.
    @Transactional
    public void reduceAllWaitingOrders(UserCredentials user, Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findByIdOrThrow(restaurant.getOwner().getId());

        if (!user.getEmail().equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        List<Waiting> waitingList = waitingRepository.findALLByRestaurantIdOrThrow(restaurantId);

        List<Waiting> waitingArrayList = new ArrayList<>();
        for (Waiting waiting : waitingList) {
            waiting.reduceWaitingOrder();
            if (waiting.getWaitingOrder() == 0) {

                //--------------
                // 알람 메서드 위치
                //--------------
                String message = "손님, 가게로 입장해 주세요.";
                notificationController.notifyCustomer(waiting.getId().toString(), message);
//                waitingRepository.delete(waiting);
                waiting.delete(LocalDateTime.now(), WaitingStatus.SEATED);
            } else {
                waitingArrayList.add(waiting);
            }

        }
        // 쿼리가 계속 나감...
        // 한꺼번에 범위로 줄일 수 있음
        waitingRepository.saveAll(waitingArrayList);
    }

    // 웨이팅 하나만 삭제하고 뒤 웨이팅 숫자 하나씩 감소
    @Transactional
    public void reduceOneWaitingOrders(UserCredentials user, Long waitingId) {

        validateWaitingRequest(user, waitingId);

        reduceWaitingOrders(waitingId, "reduce");
    }

    // 가게용 api
    // 예약 인원수와 요청사항만 변경 가능함 ( 추후 합의를 통해 ?건 이하의 순서일 때는 수정하지 못하도록 로직 수정 필요)
    @Transactional
    public WaitingResponseDto updateWaiting(UserCredentials user, Long waitingId,
        UpdateWaitingRequestDto updateWaitingRequestDto) {

        validateWaitingRequest(user, waitingId);

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        waiting.update(updateWaitingRequestDto.getPartySize(), updateWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);
        return WaitingResponseDto.of(waiting);
    }

    // 취소 메서드
    @Transactional
    public void deleteWaiting(UserCredentials user, Long waitingId) {

        validateWaitingRequest(user, waitingId);

        reduceWaitingOrders(waitingId, "delete");
    }

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

    // 페이지 네이션 말고 list로 하면 무슨 장점이 있을까요?
    @Transactional(readOnly = true)
    public Page<WaitingResponseDto> getWaitingsByRestaurantId(UserCredentials user,
        Long restaurantId, Pageable pageable) {

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
            .map(this::convertToDto).toList();
        return new PageImpl<>(waitingResponseDtos, pageable, waitingPage.getTotalElements());
    }

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
            .map(this::convertToDto).toList();
        return new PageImpl<>(waitingResponseDtos, sortedPageable, waitingPage.getTotalElements());
    }

    private WaitingResponseDto convertToDto(Waiting waiting) {
        return WaitingResponseDto.of(waiting);
    }

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
                waiting.delete(LocalDateTime.now(), waitingStatus);

                flag = true;
            } else if (flag) {
                waiting.reduceWaitingOrder();
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
