package com.sparta.goodbite.domain.notification.controller;

import com.sparta.goodbite.domain.customer.entity.Customer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server-events")
public class NotificationController {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


    @RequestMapping(value = "/subscribe/{restaurantId}", produces = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@PathVariable String restaurantId) {
        SseEmitter sseEmitter = new SseEmitter(3600000L); // 기본 30초
        sseEmitter.onCompletion(() -> removeEmitter(restaurantId, sseEmitter));
        sseEmitter.onTimeout(() -> removeEmitter(restaurantId, sseEmitter));
        sseEmitter.onError((e) -> removeEmitter(restaurantId, sseEmitter));

        emitters.computeIfAbsent(restaurantId, k -> new CopyOnWriteArrayList<>()).add(sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sseEmitter;
    }

    private void removeEmitter(String restaurantId, SseEmitter sseEmitter) {
        List<SseEmitter> restaurantEmitters = emitters.get(restaurantId);
        if (restaurantEmitters != null) {
            restaurantEmitters.remove(sseEmitter);
        }
    }

    // Method to dispatch events to all clients subscribed to a specific restaurant
    public void dispatchEvent(String restaurantId, String message) {
        List<SseEmitter> restaurantEmitters = emitters.get(restaurantId);
        if (restaurantEmitters != null) {
            for (SseEmitter emitter : restaurantEmitters) {
                try {
                    emitter.send(SseEmitter.event().name("message").data(message));
                } catch (IOException e) {
                    restaurantEmitters.remove(emitter);
                }
            }
        }
    }

    // Method to be called when a customer registers for waiting
    @PostMapping("/notify-owner/{restaurantId}")
    public void notifyOwner(@PathVariable String restaurantId, @RequestBody Customer customer) {
        String message = "New customer waiting: Name: " + customer.getNickname() + ", Email: "
            + customer.getEmail();
        dispatchEvent(restaurantId, message);
    }
}


