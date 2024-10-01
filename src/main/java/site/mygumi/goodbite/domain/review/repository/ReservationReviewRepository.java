package site.mygumi.goodbite.domain.review.repository;

import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.exception.review.ReviewErrorCode;
import site.mygumi.goodbite.exception.review.detail.ReviewNotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationReviewRepository extends JpaRepository<ReservationReview, Long> {

    List<ReservationReview> findAllByRestaurantId(Long restaurantId);

    List<ReservationReview> findAllByCustomerId(Long customerId);

    default ReservationReview findByIdOrThrow(Long waitingReviewId) {
        return findById(waitingReviewId).orElseThrow(
            () -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }
}