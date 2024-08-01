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
import org.springframework.security.access.prepost.PreAuthorize;
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

    // 손님만 등록
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/waitings")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> createWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @Valid @RequestBody PostWaitingRequestDto postWaitingRequestDto
    ) {
        return ResponseUtil.createOk(
            waitingService.createWaiting(userDetails.getUser(), postWaitingRequestDto));
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/waitings")
    public ResponseEntity<DataResponseDto<Page<WaitingResponseDto>>> getWaitingList(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseUtil.createOk(
            waitingService.getWaitings(userDetails.getUser(),
                pageable));
    }

    // 웨이팅 전체 조회용 api
    // 해당 가게 오너 + Admin
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @GetMapping("/restaurants/{restaurantId}/waitingList")
    public ResponseEntity<DataResponseDto<Page<WaitingResponseDto>>> getWaitingList(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long restaurantId,
        @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseUtil.createOk(
            waitingService.getWaitingsByRestaurantId(userDetails.getUser(), restaurantId,
                pageable));
    }

    // 단순 가게의 현재 웨이팅 갯수 받아오는 api
    // api 권한 제한 없음
    @GetMapping("/restaurants/{restaurantId}/waitings")
    public ResponseEntity<DataResponseDto<Long>> getWaitingLastNumber(
        @PathVariable Long restaurantId
    ) {
        return ResponseUtil.findOk(
            waitingService.findLastOrderNumber(restaurantId)
        );
    }

    // 웨이팅 단일 조회용 api
    // 해당 가게 오너 또는 해당 웨이팅 등록 손님 + Admin
    @GetMapping("/waitings/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> getWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId
    ) {
        return ResponseUtil.findOk(waitingService.getWaiting(userDetails.getUser(), waitingId));
    }

    // 가게 주인용 가게 전체 하나씩 웨이팅 줄이기 메서드 호출
    // 해당 가게 오너만 가능
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @PutMapping("/restaurants/{restaurantId}/waitings")
    public ResponseEntity<MessageResponseDto> reduceAllWaitingOrders(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long restaurantId
    ) {
        waitingService.reduceAllWaitingOrders(userDetails.getUser(), restaurantId);
        return ResponseUtil.updateOk();
    }

    // 가게 주인용 하나 선택 후 웨이팅 줄이기 메서드 호출
    // 해당 가게 오너 + Admin
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @PutMapping("/restaurants/waitings/{waitingId}")
    public ResponseEntity<MessageResponseDto> reduceOneWaitingOrders(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId
    ) {
        waitingService.reduceOneWaitingOrders(userDetails.getUser(), waitingId);
        return ResponseUtil.updateOk();
    }

    // 가게용 웨이팅 정보 업데이트
    // 해당 가게 오너 또는 해당 웨이팅 등록 손님 + Admin
    @PatchMapping("/waitings/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> updateWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId,
        @Valid @RequestBody UpdateWaitingRequestDto updateWaitingRequestDto
    ) {

        return ResponseUtil.updateOk(waitingService.updateWaiting(userDetails.getUser(), waitingId,
            updateWaitingRequestDto));
    }

    // 가게/손님용 취소
    // 해당 가게 오너 또는 해당 웨이팅 등록 손님 + Admin
    @DeleteMapping("/waitings/{waitingId}")
    public ResponseEntity<MessageResponseDto> deleteWaiting(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long waitingId
    ) {
        waitingService.deleteWaiting(userDetails.getUser(), waitingId);
        return ResponseUtil.deleteOk();
    }

}
