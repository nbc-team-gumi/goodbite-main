package com.sparta.goodbite.domain.waiting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send/{email}") // 메시지를 받을 때 호출되는 메서드
//    @SendTo("/queue/{email}") // 구독하고 있는 장소로 메시지 전송
    //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public void sendCustomerNotification(@DestinationVariable String email, String message) {
        String destination = "/user/" + email + "/queue/customer-alerts";
        messagingTemplate.convertAndSend(destination, message);
    }

    public void sendOwnerNotification(String email, String message) {
        String destination = "/user/" + email + "/queue/owner-alerts";
        messagingTemplate.convertAndSend(destination, message);
    }
}
