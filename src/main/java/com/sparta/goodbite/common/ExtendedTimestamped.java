package com.sparta.goodbite.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class ExtendedTimestamped extends Timestamped {

    @Column(name = "deleted_date")
    private LocalDateTime deletedAt;
}