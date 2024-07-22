package com.sparta.goodbite.domain.waiting.controller;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.domain.waiting.dto.WaitingRequestDto;
import com.sparta.goodbite.domain.waiting.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/waitings")
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createWaiting(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody WaitingRequestDto waitingRequestDto
    ) {

        return waitingService.createWaiting(waitingRequestDto);
    }

}
