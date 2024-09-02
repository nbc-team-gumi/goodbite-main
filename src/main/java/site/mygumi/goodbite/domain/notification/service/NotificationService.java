package site.mygumi.goodbite.domain.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationService {

    public void removeEmitter(Map<String, List<SseEmitter>> emitterMap, String id,
        SseEmitter sseEmitter) {
        List<SseEmitter> emitters = emitterMap.get(id);
        if (emitters != null) {
            emitters.remove(sseEmitter);
        }
    }

    // 이벤트 전송 메서드: 특정 식당에 이벤트 전송
    public void dispatchEventToRestaurant(Map<String, List<SseEmitter>> restaurantEmitters,
        String restaurantId, String message) {
        dispatchEvent(restaurantEmitters, restaurantId, message);
    }

    // 이벤트 전송 메서드: 특정 대기 번호에 이벤트 전송
    public void dispatchEventToWaiting(Map<String, List<SseEmitter>> waitingEmitters,
        String waitingId, String message) {
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

}
