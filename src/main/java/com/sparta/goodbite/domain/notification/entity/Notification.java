package com.sparta.goodbite.domain.notification.entity;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Notification implements Serializable {

    private Long postId;
    private String message;
    private Long timestamp;
    private Long writerId;

    public Notification(Long postId, String message, Long writerId) {
        this.postId = postId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.writerId = writerId;
    }
}
