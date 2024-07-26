package com.sparta.goodbite.domain.waiting.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.WaitingResponseDto;
import com.sparta.goodbite.domain.waiting.service.WaitingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/waitings")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> createWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @Valid @RequestBody PostWaitingRequestDto postWaitingRequestDto
    ) {
        return ResponseUtil.createOk(
            waitingService.createWaiting(userDetails, postWaitingRequestDto));
    }

    // 웨이팅 전체 조회용 api
    @GetMapping("/restaurants/{restaurantId}/waitingList")
    public ResponseEntity<DataResponseDto<Page<WaitingResponseDto>>> getWaitingList(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long restaurantId,
        @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseUtil.createOk(
            waitingService.getWaitingsByRestaurantId(userDetails, restaurantId, pageable));
    }

    @GetMapping("/restaurants/{restaurantId}/waitings")
    public ResponseEntity<DataResponseDto<Long>> getWaitingLastNumber(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long restaurantId
    ) {
        return ResponseUtil.findOk(
            waitingService.findLastOrderNumber(restaurantId)
        );
    }

    // 웨이팅 단일 조회용 api
    @GetMapping("/waitings/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> getWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId
    ) {
        return ResponseUtil.findOk(waitingService.getWaiting(userDetails, waitingId));
    }

    // 가게 주인용 가게 전체 하나씩 웨이팅 줄이기 메서드 호출
    @PutMapping("/restaurants/{restaurantId}/waitings")
    public ResponseEntity<MessageResponseDto> reduceAllWaitingOrders(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long restaurantId
    ) {
        waitingService.reduceAllWaitingOrders(userDetails, restaurantId);
        return ResponseUtil.updateOk();
    }

    // 가게 주인용 하나 선택 후 웨이팅 줄이기 메서드 호출
    @PutMapping("/restaurants/waitings/{waitingId}")
    public ResponseEntity<MessageResponseDto> reduceOneWaitingOrders(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId
    ) {
        waitingService.reduceOneWaitingOrders(userDetails, waitingId);
        return ResponseUtil.updateOk();
    }

    // 가게용 웨이팅 정보 업데이트
    @PatchMapping("/waitings/{waitingId}")
    public ResponseEntity<MessageResponseDto> updateWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId,
        @Valid @RequestBody UpdateWaitingRequestDto updateWaitingRequestDto
    ) {
        waitingService.updateWaiting(userDetails, waitingId, updateWaitingRequestDto);
        return ResponseUtil.updateOk();
    }

    // 가게/손님용 취소
    @DeleteMapping("/waitings/{waitingId}")
    public ResponseEntity<MessageResponseDto> deleteWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId
    ) {
        waitingService.deleteWaiting(userDetails, waitingId);
        return ResponseUtil.deleteOk();
    }

}