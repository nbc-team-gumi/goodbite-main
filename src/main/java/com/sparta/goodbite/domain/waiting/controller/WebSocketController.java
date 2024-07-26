package com.sparta.goodbite.domain.waiting.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public String notify(String message) {
        return message;
    }
}