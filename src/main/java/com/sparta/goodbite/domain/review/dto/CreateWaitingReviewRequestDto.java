package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.review.entity.WaitingReview;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateWaitingReviewRequestDto extends CreateReviewRequestDto {

    @NotNull(message = "웨이팅 ID를 입력해 주세요.")
    private Long waitingId;

    public WaitingReview toEntity(Restaurant restaurant, Customer customer, Waiting waiting) {
        return WaitingReview.builder()
            .rating(rating)
            .content(content)
            .restaurant(restaurant)
            .customer(customer)
            .waiting(waiting)
            .build();
    }
}