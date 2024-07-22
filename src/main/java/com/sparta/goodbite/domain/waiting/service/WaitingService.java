package com.sparta.goodbite.domain.waiting.service;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.domain.user.entity.Customer;
import com.sparta.goodbite.domain.user.repository.CustomerRepository;
import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.WaitingResponseDto;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingType;
import com.sparta.goodbite.domain.waiting.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;


    public WaitingResponseDto createWaiting(
//        UserDetailsImpl userDetails,
        PostWaitingRequestDto postWaitingRequestDto) {
        Long waitingOrder = 1L; // 추후 List에서 마지막 번호 반환 후 +1해서 저장하도록 수정 필요

        Restaurant restaurant = restaurantRepository.findById(
            postWaitingRequestDto.getRestaurantId()).orElseThrow();

        Customer customer = customerRepository.findById(1L)
            .orElseThrow(); // 추후 UserDetails 받고 수정 필요

        Waiting waiting = new Waiting(
            restaurant,
            customer,
            waitingOrder,
            WaitingStatus.WAITING, //생성 시 무조껀 Waiting
            postWaitingRequestDto.getPartySize(),
            WaitingType.OFFLINE,
            postWaitingRequestDto.getDemand());
        System.out.println(waiting.getWaitingType());

        waitingRepository.save(waiting);

        return WaitingResponseDto.from(waiting, restaurant.getName());
    }

//    public WaitingResponseDto getWaiting(Long waitingId) {
//
//        return WaitingResponseDto.from(waitingRepository.findById(waitingId).orElseThrow());
//    }

    //가게 주인용 api
    // 해당 메서드 동작 시, 가게의 id가 들어간 orders가 하나씩 줄게 된다.
    public WaitingResponseDto updateWaitingOrders(
//        UserDetailsImpl userDetails,
        Long waitingId,
        UpdateWaitingRequestDto updateWaitingRequestDto) {

    }


    // 손님용 api
    // 예약 인원수와 요청사항만 변경 가능함 ( 추후 합의를 통해 ?건 이하의 순서일 때는 수정하지 못하도록 로직 수정 필요)
    public WaitingResponseDto updateWaiting(Long waitingId,
//        UserDetailsImpl userDetails,
        UpdateWaitingRequestDto updateWaitingRequestDto) {

        Waiting waiting = waitingRepository.findById(waitingId).orElseThrow();
        String restaurantName = waiting.getRestaurant().getName();

        waiting.update(updateWaitingRequestDto.getPartySize(), updateWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);
        return WaitingResponseDto.from(waiting, restaurantName);
    }


    // 삭제 시, 메서드를 호출한 유저의 id와 취소하고자 하는 가게 id를 받아야 하는거 아닌가?
    // 프론트에서 어떤값을 주느냐에 따라 달라질 것 같다.
    public WaitingResponseDto deleteWaiting(Long waitingId) {
//        UserDetailsImpl userDetails,
        Waiting waiting = waitingRepository.findById(waitingId).orElseThrow();
        String restaurantName = waiting.getRestaurant().getName();

        waitingRepository.delete(waiting);
        return WaitingResponseDto.from(waiting, restaurantName);
    }


}
