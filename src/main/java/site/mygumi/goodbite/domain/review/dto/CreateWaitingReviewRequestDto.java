package site.mygumi.goodbite.domain.review.dto;

import site.mygumi.goodbite.domain.customer.entity.Customer;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.review.entity.WaitingReview;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
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