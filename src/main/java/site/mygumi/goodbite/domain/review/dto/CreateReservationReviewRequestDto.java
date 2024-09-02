package site.mygumi.goodbite.domain.review.dto;

import site.mygumi.goodbite.domain.customer.entity.Customer;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateReservationReviewRequestDto extends CreateReviewRequestDto {

    @NotNull(message = "예약 ID를 입력해 주세요.")
    private Long reservationId;

    public ReservationReview toEntity(Restaurant restaurant, Customer customer,
        Reservation reservation) {

        return ReservationReview.builder()
            .rating(rating)
            .content(content)
            .restaurant(restaurant)
            .customer(customer)
            .reservation(reservation)
            .build();
    }
}