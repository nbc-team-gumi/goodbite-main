package com.sparta.goodbite.domain.waiting.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.customer.repository.CustomerRepository;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.WaitingResponseDto;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import com.sparta.goodbite.domain.waiting.repository.WaitingRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;
import com.sparta.goodbite.exception.waiting.WaitingErrorCode;
import com.sparta.goodbite.exception.waiting.WaitingException;
import com.sparta.goodbite.exception.waiting.detail.WaitingNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OwnerRepository ownerRepository;
    private final RedissonClient redissonClient;

    public WaitingResponseDto createWaiting(UserCredentials user,
        PostWaitingRequestDto postWaitingRequestDto) {

        RLock lock = redissonClient.getLock(//분산락 객체 생성
            "createWaitingLock:"
                //Redisson을 사용하여 레스토랑 ID 기반의 락을 생성합니다. 이는 특정 레스토랑에 대한 웨이팅 등록이 동시에 발생하지 않도록 합니다.
                + postWaitingRequestDto.getRestaurantId());//락이름을 creatwaitingLock:1(아이디가1일때)=> 각 아이디에 해당하는 개별락을 설정
        lock.lock(); //해당락을 획득. 다른스레드가 이미 이 락을 가지고 있으면 이 락이 해제될때까지 블록(대기함). 블로킹호출. 락을 획득할때까지 현재스레드는 대기
        //락을 획득합니다. 이 메서드는 다른 스레드가 이 락을 해제할 때까지 기다립니다.

        //락획득후 실행할 코드. 실제로 내가 보호하고자하는 작업을 수행ㄹ. 동시에 싱핼하면 안되는 코드영역.
        //이영역은 공유자원에 대한 접근이나 중요한 작업을 수행한다.
        //지금의 경우 새 웨이팅 정보를 데이터베이스에 추가하는 작업
//try { ... } finally { lock.unlock(); } -> 락을 획득한 상태에서 웨이팅 등록 로직을 수행합니다. 모든 작업이 끝난 후에는 반드시 락을 해제합니다.
        try {
            Restaurant restaurant = restaurantRepository.findByIdOrThrow(
                postWaitingRequestDto.getRestaurantId());

            Customer customer = customerRepository.findByIdOrThrow(user.getId());

            waitingRepository.validateRestaurantIdAndCustomerId(restaurant.getId(),
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

            return WaitingResponseDto.of(waiting);

        } finally {//이 작업은 try 블록 내에서 예외가 발생하더라도 반드시 실행됩니다. 이렇게 함으로써 락을 획득한 후에는 반드시 락을 해제하여 다른 스레드가 락을 획득할 수 있도록 합니다.
//만약 finally 블록이 없다면, 예외 발생 시 락이 해제되지 않아 다른 스레드가 영구적으로 락을 획득하지 못하게 될 수 있습니다. 이를 방지하기 위해 항상 finally 블록에서 락을 해제합니다.
            //예외가 발생하더라도 반드시 실행되기 때문에, 락을 안전하게 해제할 수 있습니다.
            //임계 구역 내에서 데이터베이스 작업을 수행하다가 예외가 발생해도, finally 블록이 실행되어 락이 해제됩니다. 이를 통해 다른 스레드가 계속해서 락을 획득할 수 있도록 보장합니다.
            lock.unlock();
        }
    }

    // 단일 조회용 메서드
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

                sendNotificationToCustomer(waiting.getCustomer().getId(),
                    "가게로 들어와 주세요.");
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
    public WaitingResponseDto updateWaiting(UserCredentials user, Long waitingId,
        UpdateWaitingRequestDto updateWaitingRequestDto) {

        validateWaitingRequest(user, waitingId);

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        waiting.update(updateWaitingRequestDto.getPartySize(), updateWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);
        return WaitingResponseDto.of(waiting);
    }

    // 취소 메서드
    public void deleteWaiting(UserCredentials user, Long waitingId) {

        validateWaitingRequest(user, waitingId);

        reduceWaitingOrders(waitingId, "delete");
    }

    public Long findLastOrderNumber(Long restaurantId) {
        if (!waitingRepository.findALLByRestaurantId(restaurantId).isEmpty()) {
            // 해당하는 레스토랑에 예약이 하나라도 존재한다면
            return waitingRepository.findMaxWaitingOrderByRestaurantId(
                restaurantId);
        }
        // 해당하는 레스토랑에 예약이 하나도 없다
        return 0L;
    }

    // 페이지 네이션 말고 list로 하면 무슨 장점이 있을까요?
    public Page<WaitingResponseDto> getWaitingsByRestaurantId(UserCredentials user,
        Long restaurantId, Pageable pageable) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findById(restaurant.getOwner().getId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));

        // api 요청한 유저가 해당 레스토랑의 '오너'와 같지 않다면
        if (!user.getEmail().equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Page<Waiting> waitingPage = waitingRepository.findByRestaurantId(restaurantId, pageable);

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(this::convertToDto).toList();
        return new PageImpl<>(waitingResponseDtos, pageable, waitingPage.getTotalElements());
    }

    public Page<WaitingResponseDto> getWaitings(UserCredentials user, Pageable pageable) {

        Page<Waiting> waitingPage = waitingRepository.findByCustomerId(user.getId(), pageable);

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(this::convertToDto).toList();
        return new PageImpl<>(waitingResponseDtos, pageable, waitingPage.getTotalElements());
    }

    private WaitingResponseDto convertToDto(Waiting waiting) {
        return WaitingResponseDto.of(waiting);
    }

    private void reduceWaitingOrders(Long waitingId, String type) {
        Waiting waitingOne = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        List<Waiting> waitingList = waitingRepository.findALLByRestaurantId(
            waitingOne.getRestaurant().getId());

        String message = "";
        boolean flag = false;
        List<Waiting> waitingArrayList = new ArrayList<>();

        for (Waiting waiting : waitingList) {
            if (Objects.equals(waiting.getId(), waitingId)) {

                //--------------
                // 알람 메서드 위치
                //--------------

                if (type.equals("delete")) {
                    message = "웨이팅이 취소되었습니다.";
                    waiting.delete(LocalDateTime.now(), WaitingStatus.CANCELLED);
                } else if (type.equals("reduce")) {
                    message = "손님, 가게로 입장해 주세요.";
                    waiting.delete(LocalDateTime.now(), WaitingStatus.SEATED);
                }
                sendNotificationToCustomer(waiting.getCustomer().getId(), message);
//                waitingRepository.delete(waiting);

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

    private void sendNotificationToCustomer(Long customerId, String message) {
        messagingTemplate.convertAndSend("/topic/notifications/" + customerId, message);
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

        List<Waiting> waitingList = waitingRepository.findALLByRestaurantId(restaurant.getId());
        if (waitingList.isEmpty()) {
            throw new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND);
        }
    }
}
