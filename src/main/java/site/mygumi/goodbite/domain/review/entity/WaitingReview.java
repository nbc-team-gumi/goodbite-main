package site.mygumi.goodbite.domain.review.entity;

import site.mygumi.goodbite.domain.customer.entity.Customer;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WaitingReview extends Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waiting_id", nullable = false)
    private Waiting waiting;

    @Builder
    public WaitingReview(double rating, String content, Restaurant restaurant, Customer customer,
        Waiting waiting) {

        super(rating, content, restaurant, customer);
        this.waiting = waiting;
    }
}