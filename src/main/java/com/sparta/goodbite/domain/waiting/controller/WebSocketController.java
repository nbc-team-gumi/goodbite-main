package com.sparta.goodbite.domain.waiting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;

    // stomp 테스트 화면
    @GetMapping("/alarm/stomp")
    public String stompAlarm() {
        return "/stomp";
    }

    @MessageMapping("/{userEmail}")
    public void message(@DestinationVariable("userEmail") String userEmail) {
        messagingTemplate.convertAndSend("/sub/" + userEmail, "alarm socket connection completed.");
    }
}