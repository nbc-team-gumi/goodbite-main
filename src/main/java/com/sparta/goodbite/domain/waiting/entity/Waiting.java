package com.sparta.goodbite.domain.waiting.entity;

import com.sparta.goodbite.common.ExtendedTimestamped;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Waiting extends ExtendedTimestamped {

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

    public void reduceWaitingOrder() {
        --this.waitingOrder;
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
