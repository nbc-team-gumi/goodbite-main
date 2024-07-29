package com.sparta.goodbite.domain.review.entity;

import com.sparta.goodbite.common.Timestamped;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float rating;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Builder
    public Review(float rating, String content, Restaurant restaurant, Customer customer) {
        this.rating = rating;
        this.content = content;
        this.restaurant = restaurant;
        this.customer = customer;
    }

    public void update(UpdateReviewRequestDto updateReviewRequestDto) {
        this.rating =
            updateReviewRequestDto.getRating() != null ? updateReviewRequestDto.getRating()
                : this.rating;
        this.content =
            updateReviewRequestDto.getContent() != null ? updateReviewRequestDto.getContent()
                : this.content;
    }
}