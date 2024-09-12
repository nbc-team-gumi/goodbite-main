package site.mygumi.goodbite.domain.waiting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mygumi.goodbite.common.entity.ExtendedTimestamped;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;

@Getter
@NoArgsConstructor
@Entity
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

    private Long waitingOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingStatus status;

    private Long partySize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingType waitingType;

    private String demand;

    @Builder
    public Waiting(Restaurant restaurant, Customer customer, Long waitingOrder,
        WaitingStatus status, Long partySize, WaitingType waitingType,
        String demand) {
        this.restaurant = restaurant;
        this.customer = customer;
        this.waitingOrder = waitingOrder;
        this.status = status;
        this.partySize = partySize;
        this.waitingType = waitingType;
        this.demand = demand;
    }

    public void update(Long partySize, String demand) {
        this.partySize = partySize;
        this.demand = demand;
    }

    public void delete(LocalDateTime deletedAt, WaitingStatus status) {
        this.waitingOrder = null;
        this.deletedAt = deletedAt;
        this.status = status;
    }

    public void decrementWaitingOrder() {
        --this.waitingOrder;
    }

    public boolean canSubmitReview() {
        return this.status == WaitingStatus.SEATED && this.deletedAt != null
            && ChronoUnit.MINUTES.between(deletedAt, LocalDateTime.now()) <= 60 * 24 * 3;
    }

    public enum WaitingStatus {

        WAITING,
        SEATED,
        CANCELLED
    }

    public enum WaitingType {

        OFFLINE,
        ONLINE
    }
}
