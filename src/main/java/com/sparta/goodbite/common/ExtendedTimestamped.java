package com.sparta.goodbite.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public class ExtendedTimestamped extends Timestamped {

    @Column(name = "delete_date")
    private LocalDateTime deletedAt;
}