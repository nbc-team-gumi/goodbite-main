package com.sparta.goodbite.domain.waiting.entity;

import com.sparta.goodbite.common.ExtendedTimestamped;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.user.entity.Customer;
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
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private Long waitingOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingStatus status;

    private Long partySize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingType waitingType;

    private String demand;

    public Waiting(Long waitingOrder, WaitingStatus status, Long partySize, WaitingType waitingType,
        String demand) {
        this.waitingOrder = waitingOrder;
        this.status = status;
        this.partySize = partySize;
        this.waitingType = waitingType;
        this.demand = demand;
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
