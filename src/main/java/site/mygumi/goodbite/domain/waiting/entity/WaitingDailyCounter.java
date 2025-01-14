package site.mygumi.goodbite.domain.waiting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "waiting_daily_counter",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_waiting_counter_restaurant_date",
            columnNames = {"restaurant_id", "date"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingDailyCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long restaurantId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer lastSequenceNumber = 0;

    @Version
    private Long version;

    @Builder
    private WaitingDailyCounter(Long restaurantId, LocalDate date, Integer lastSequenceNumber) {
        this.restaurantId = restaurantId;
        this.date = date;
        this.lastSequenceNumber = lastSequenceNumber;
    }

    public static WaitingDailyCounter createCounter(Long restaurantId, LocalDate date) {
        return WaitingDailyCounter.builder()
            .restaurantId(restaurantId)
            .date(date)
            .lastSequenceNumber(0)
            .build();
    }

    public Integer getNextNumber() {
        return ++this.lastSequenceNumber;
    }
}
