package site.mygumi.goodbite.domain.operatinghour.entity;

import site.mygumi.goodbite.common.entity.Timestamped;
import site.mygumi.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.DayOfWeek;
import java.time.LocalTime;
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

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime openTime;
    private LocalTime closeTime;

    @Builder
    public OperatingHour(Restaurant restaurant, DayOfWeek dayOfWeek, LocalTime openTime,
        LocalTime closeTime) {
        this.restaurant = restaurant;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public void update(UpdateOperatingHourRequestDto updateOperatingHourRequestDto) {
        this.openTime = updateOperatingHourRequestDto.getOpenTime();
        this.closeTime = updateOperatingHourRequestDto.getCloseTime();
    }
}