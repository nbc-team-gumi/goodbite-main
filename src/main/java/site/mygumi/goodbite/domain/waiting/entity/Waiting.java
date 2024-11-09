package site.mygumi.goodbite.domain.waiting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mygumi.goodbite.common.entity.ExtendedTimestamped;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;

@Entity
@Table(indexes = {
    @Index(name = "idx_waiting_restaurant_status_created",
        columnList = "restaurant_id,status,created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Waiting extends ExtendedTimestamped {

    public static final int DEFAULT_PAGE_SIZE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Integer waitingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingStatus status;

    private Long partySize;

    private String demand;

    @Builder
    public Waiting(Restaurant restaurant, Customer customer, Integer waitingNumber,
        WaitingStatus status, Long partySize, String demand) {
        this.restaurant = restaurant;
        this.customer = customer;
        this.waitingNumber = waitingNumber;
        this.status = status;
        this.partySize = partySize;
        this.demand = demand;
    }

    public void update(Long partySize, String demand) {
        this.partySize = partySize;
        this.demand = demand;
    }

    public void enter() {
        this.deletedAt = LocalDateTime.now();
        this.status = WaitingStatus.ENTERED;
    }

    public void noShow() {
        this.deletedAt = LocalDateTime.now();
        this.status = WaitingStatus.NO_SHOW;
    }

    public void cancel() {
        this.deletedAt = LocalDateTime.now();
        this.status = WaitingStatus.CANCELLED;
    }

    public boolean canSubmitReview() {
        return this.status == WaitingStatus.ENTERED && this.deletedAt != null
            && ChronoUnit.MINUTES.between(deletedAt, LocalDateTime.now()) <= 60 * 24 * 3;
    }

    public enum WaitingStatus {
        WAITING,
        ENTERED,
        CANCELLED,
        NO_SHOW,
        EXPIRED
    }
}
