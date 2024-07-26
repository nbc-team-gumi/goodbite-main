package com.sparta.goodbite.domain.waiting.service;

import com.sparta.goodbite.domain.Customer.entity.Customer;
import com.sparta.goodbite.domain.Customer.repository.CustomerRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.WaitingResponseDto;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import com.sparta.goodbite.domain.waiting.repository.WaitingRepository;
import com.sparta.goodbite.exception.waiting.WaitingErrorCode;
import com.sparta.goodbite.exception.waiting.WaitingException;
import com.sparta.goodbite.exception.waiting.detail.WaitingNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Long, SseEmitter> emitters = new HashMap<>();
    private final Map<Long, Integer> waitingList = new HashMap<>();

    public WaitingResponseDto createWaiting(
//        UserDetailsImpl userDetails,
        PostWaitingRequestDto postWaitingRequestDto) {

        Restaurant restaurant = restaurantRepository.findById(
            postWaitingRequestDto.getRestaurantId()).orElseThrow();

        Customer customer = customerRepository.findById(2L)
            .orElseThrow(); // 추후 UserDetails 받고 수정 필요

        Waiting waitingDuplicated = waitingRepository.findByRestaurantIdAndCustomerId(
            restaurant.getId(),
            customer.getId());

        if (waitingDuplicated != null) {
            throw new WaitingException(WaitingErrorCode.WAITING_DUPLICATED);
        }

        Long LastOrderNumber = findLastOrderNumber(restaurant.getId());

        Waiting waiting = new Waiting(
            restaurant,
            customer,
            LastOrderNumber + 1,
            WaitingStatus.WAITING, //생성 시 무조껀 Waiting
            postWaitingRequestDto.getPartySize(),
            postWaitingRequestDto.getWaitingType(),
            postWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);

        return WaitingResponseDto.of(waiting, restaurant.getName());
    }

    // 단일 조회용 메서드
    public WaitingResponseDto getWaiting(Long waitingId) {

        Waiting waiting = waitingRepository.findById(waitingId)
            .orElseThrow(() -> new WaitingNotFoundException(
                WaitingErrorCode.WAITING_NOT_FOUND));
        return WaitingResponseDto.of(waiting, waiting.getRestaurant().getName());
    }

    //    //가게 주인용 api
//    //해당 메서드 동작 시, 가게의 id가 들어간 orders가 하나씩 줄게 된다.
    // restaurant id에 맞는 Waiting들의 order를 하나씩 줄인다.
    @Transactional
    public void reduceAllWaitingOrders(
//        UserDetailsImpl userDetails,
        Long restaurantId) {
        List<Waiting> waitingList = waitingRepository.findALLByRestaurantId(restaurantId);
        if (waitingList.isEmpty()) {
            throw new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND);
        }

        List<Waiting> waitingArrayList = new ArrayList<>();

        for (Waiting waiting : waitingList) {
            waiting.reduceWaitingOrder();
            if (waiting.getWaitingOrder() == 0) {
                // 그리고 여기서 알람이나 그런거 해야 함
                System.out.println("0번은 입장하세요~");
                //알람!
                waitingRepository.delete(waiting);
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
    public void reduceOneWaitingOrders(
        //        UserDetailsImpl userDetails,
        Long waitingId) {
        reduceWaitingOrders(waitingId, "reduce");
    }

    // 가게용 api
    // 예약 인원수와 요청사항만 변경 가능함 ( 추후 합의를 통해 ?건 이하의 순서일 때는 수정하지 못하도록 로직 수정 필요)
    public WaitingResponseDto updateWaiting(Long waitingId,
//        UserDetailsImpl userDetails,
        UpdateWaitingRequestDto updateWaitingRequestDto) {

        Waiting waiting = waitingRepository.findById(waitingId)
            .orElseThrow(() -> new WaitingNotFoundException(
                WaitingErrorCode.WAITING_NOT_FOUND));
        String restaurantName = waiting.getRestaurant().getName();

        waiting.update(updateWaitingRequestDto.getPartySize(), updateWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);
        return WaitingResponseDto.of(waiting, restaurantName);
    }

    // 삭제 시, 메서드를 호출한 유저의 id와 취소하고자 하는 가게 id를 받아야 하는거 아닌가?
    // 프론트에서 어떤값을 주느냐에 따라 달라질 것 같다.
    public void deleteWaiting(
//        UserDetailsImpl userDetails,
        Long waitingId) {
        reduceWaitingOrders(waitingId, "delete");
    }

    public Long findLastOrderNumber(
        Long restaurantId) {
        if (!waitingRepository.findALLByRestaurantId(restaurantId).isEmpty()) {
            // 해당하는 레스토랑에 예약이 하나라도 존재한다면
            return waitingRepository.findMaxWaitingOrderByRestaurantId(
                restaurantId);
        }
        // 해당하는 레스토랑에 예약이 하나도 없다
        return 0L;

    }

    public Page<WaitingResponseDto> getWaitingsByRestaurantId(Long restaurantId,
        Pageable pageable) {
        Page<Waiting> waitingPage = waitingRepository.findByRestaurantId(restaurantId, pageable);

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return new PageImpl<>(waitingResponseDtos, pageable, waitingPage.getTotalElements());
    }

    private WaitingResponseDto convertToDto(Waiting waiting) {
        return WaitingResponseDto.of(waiting, waiting.getRestaurant().getName());
    }

    private void reduceWaitingOrders(Long waitingId, String type) {
        Waiting waitingOne = waitingRepository.findById(waitingId)
            .orElseThrow(() -> new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND));
        List<Waiting> waitingList = waitingRepository.findALLByRestaurantId(
            waitingOne.getRestaurant().getId());

        String message = "";
        boolean flag = false;
        List<Waiting> waitingArrayList = new ArrayList<>();

        for (Waiting waiting : waitingList) {
            if (Objects.equals(waiting.getId(), waitingId)) {
                //여기서 알람 메서드
                if (type.equals("delete")) {
                    message = "웨이팅이 취소되었습니다.";
                } else if (type.equals("reduce")) {
                    message = "가게로 들어와 주세요.";
                }
                sendNotificationToCustomer(waiting.getCustomer().getId(),
                    message);
                waitingRepository.delete(waiting);
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

}
