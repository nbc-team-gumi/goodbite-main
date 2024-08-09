package com.sparta.goodbite.domain.notification.controller;

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

    // 식당별 구독 관리
    private final Map<String, List<SseEmitter>> restaurantEmitters = new ConcurrentHashMap<>();
    // 대기 번호별 구독 관리
    private final Map<String, List<SseEmitter>> waitingEmitters = new ConcurrentHashMap<>();

    // 식당 구독: 식당 아이디로 식당 구독자 관리
    @RequestMapping(value = "/subscribe/restaurant/{restaurantId}", produces = MediaType.ALL_VALUE)
    public SseEmitter subscribeToRestaurant(@PathVariable String restaurantId) {
        SseEmitter sseEmitter = new SseEmitter(3600000L); // 기본 1시간
        sseEmitter.onCompletion(() -> removeEmitter(restaurantEmitters, restaurantId, sseEmitter));
        sseEmitter.onTimeout(() -> removeEmitter(restaurantEmitters, restaurantId, sseEmitter));
        sseEmitter.onError((e) -> removeEmitter(restaurantEmitters, restaurantId, sseEmitter));

        restaurantEmitters.computeIfAbsent(restaurantId, k -> new CopyOnWriteArrayList<>())
            .add(sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sseEmitter;
    }

    // 대기 번호 구독: 고객이 자신의 대기 번호로 구독
    @RequestMapping(value = "/subscribe/waiting/{waitingId}", produces = MediaType.ALL_VALUE)
    public SseEmitter subscribeToWaiting(@PathVariable String waitingId) {
        SseEmitter sseEmitter = new SseEmitter(3600000L); // 기본 1시간
        sseEmitter.onCompletion(() -> removeEmitter(waitingEmitters, waitingId, sseEmitter));
        sseEmitter.onTimeout(() -> removeEmitter(waitingEmitters, waitingId, sseEmitter));
        sseEmitter.onError((e) -> removeEmitter(waitingEmitters, waitingId, sseEmitter));

        waitingEmitters.computeIfAbsent(waitingId, k -> new CopyOnWriteArrayList<>())
            .add(sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sseEmitter;
    }

    private void removeEmitter(Map<String, List<SseEmitter>> emitterMap, String id,
        SseEmitter sseEmitter) {
        List<SseEmitter> emitters = emitterMap.get(id);
        if (emitters != null) {
            emitters.remove(sseEmitter);
        }
    }

    // 이벤트 전송 메서드: 특정 식당에 이벤트 전송
    public void dispatchEventToRestaurant(String restaurantId, String message) {
        dispatchEvent(restaurantEmitters, restaurantId, message);
    }

    // 이벤트 전송 메서드: 특정 대기 번호에 이벤트 전송
    public void dispatchEventToWaiting(String waitingId, String message) {
        dispatchEvent(waitingEmitters, waitingId, message);
    }

    private void dispatchEvent(Map<String, List<SseEmitter>> emitterMap, String id,
        String message) {
        List<SseEmitter> emitters = emitterMap.get(id);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("message").data(message));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }
        }
    }

    // 고객이 대기 등록 시 알림
    @PostMapping("/notify-owner/{restaurantId}")
    public void notifyOwner(@PathVariable String restaurantId,
        @RequestBody String message) {
        dispatchEventToRestaurant(restaurantId, message);
    }

    // 대기 수락 시 고객에게 알림
    @PostMapping("/notify-customer/{waitingId}")
    public void notifyCustomer(@PathVariable String waitingId, @RequestBody String message) {
        dispatchEventToWaiting(waitingId, message);
    }
}
