package com.sparta.goodbite.domain.review.entity;

import com.sparta.goodbite.common.Timestamped;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class Review extends Timestamped {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private double rating;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    protected Review(double rating, String content, Restaurant restaurant, Customer customer) {
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