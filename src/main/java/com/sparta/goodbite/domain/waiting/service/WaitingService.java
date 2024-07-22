package com.sparta.goodbite.domain.waiting.service;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.waiting.dto.WaitingRequestDto;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingType;
import com.sparta.goodbite.domain.waiting.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;


    public ResponseEntity<MessageResponseDto> createWaiting(WaitingRequestDto waitingRequestDto) {
        Long waitingOrder = 1L;
        Waiting waiting = new Waiting(
            waitingOrder,
            WaitingStatus.WAITING,
            waitingRequestDto.partySize(),
            WaitingType.OFFLINE,
            waitingRequestDto.demand());
        System.out.println(waiting.getWaitingType());

        waitingRepository.save(waiting);
        return ResponseUtil.createOk();
    }
}
