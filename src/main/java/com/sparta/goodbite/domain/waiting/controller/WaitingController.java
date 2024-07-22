package com.sparta.goodbite.domain.waiting.controller;

import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import com.sparta.goodbite.domain.waiting.dto.WaitingResponseDto;
import com.sparta.goodbite.domain.waiting.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/waitings")
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> createWaiting(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody PostWaitingRequestDto postWaitingRequestDto
    ) {

        return ResponseUtil.createOk(waitingService.createWaiting(postWaitingRequestDto));
    }

//    @GetMapping("/{waitingId}")
//    public ResponseEntity<DataResponseDto<WaitingResponseDto>> getWaiting(
////        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable Long waitingId
//    ) {
//        return ResponseUtil.createOk(waitingService.getWaiting(waitingId));
//    }

    @PutMapping("/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> updateWaiting(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long waitingId,
        @RequestBody UpdateWaitingRequestDto updateWaitingRequestDto
    ) {

        return ResponseUtil.updateOk(
            waitingService.updateWaiting(waitingId, updateWaitingRequestDto));
    }

    @PutMapping("/{waitingId}/orders")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> updateWaitingOrders(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long waitingId,
        @RequestBody UpdateWaitingRequestDto updateWaitingRequestDto
    ) {
        return ResponseUtil.updateOk(
            waitingService.updateWaitingOrders(waitingId, updateWaitingRequestDto));
    }


    @DeleteMapping("/{waitingId}")
    public ResponseEntity<MessageResponseDto> deleteWaiting(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long waitingId
    ) {
        waitingService.deleteWaiting(waitingId);
        return ResponseUtil.deleteOk();
    }


}
