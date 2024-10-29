package site.mygumi.goodbite.domain.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.domain.review.exception.ReviewErrorCode;
import site.mygumi.goodbite.domain.review.exception.detail.ReviewNotFoundException;

public interface ReservationReviewRepository extends JpaRepository<ReservationReview, Long> {

    List<ReservationReview> findAllByRestaurantId(Long restaurantId);

    List<ReservationReview> findAllByCustomerId(Long customerId);

    default ReservationReview findByIdOrThrow(Long waitingReviewId) {
        return findById(waitingReviewId).orElseThrow(
            () -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }
}