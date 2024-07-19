package com.sparta.goodbite.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

    @Column(name = "create_time", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "update_date")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}