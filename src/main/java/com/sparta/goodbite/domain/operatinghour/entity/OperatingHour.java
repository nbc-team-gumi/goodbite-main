package com.sparta.goodbite.domain.operatinghour.entity;

import com.sparta.goodbite.common.Timestamped;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourRequestDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OperatingHour extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    private String dayOfWeek;
    private String openTime;
    private String closeTime;

    @Builder
    public OperatingHour(Restaurant restaurant, String dayOfWeek, String openTime,
        String closeTime) {
        this.restaurant = restaurant;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public void update(OperatingHourRequestDto operatingHourRequestDto) {
        this.dayOfWeek = operatingHourRequestDto.getDayOfWeek();
        this.openTime = operatingHourRequestDto.getOpenTime();
        this.closeTime = operatingHourRequestDto.getCloseTime();
    }
}