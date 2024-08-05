package com.sparta.goodbite.domain.waiting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final SimpMessageSendingOperations messagingTemplate;

    @SendTo("/{userEmail}/queue/customer-alerts")
    public void alarmByMessage(@DestinationVariable String userEmail, String message) {
        messagingTemplate.convertAndSend(userEmail + "/queue/", message);
    }

}
