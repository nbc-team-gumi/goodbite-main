package site.mygumi.goodbite.domain.review.service;

import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.domain.review.entity.WaitingReview;
import site.mygumi.goodbite.domain.review.repository.ReservationReviewRepository;
import site.mygumi.goodbite.domain.review.repository.WaitingReviewRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TotalReviewService {

    private final ReservationReviewRepository reservationReviewRepository;
    private final WaitingReviewRepository waitingReviewRepository;
    private final RestaurantRepository restaurantRepository;

    public Page<ReviewResponseDto> getAllReviewsSortedAndPaged(Pageable pageable,
        List<ReviewResponseDto> reservationReviews, List<ReviewResponseDto> waitingReviews) {

        List<ReviewResponseDto> allReviews = new ArrayList<>();
        allReviews.addAll(waitingReviews);
        allReviews.addAll(reservationReviews);

        allReviews.sort(Comparator.comparing(ReviewResponseDto::createdAt).reversed());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allReviews.size());
        return new PageImpl<>(allReviews.subList(start, end), pageable, allReviews.size());
    }
    
    @Transactional
    public void updateRestaurantRating(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        restaurant.updateRating(calculateAverageRating(restaurant));
    }

    // Lock 적용 필요
    private double calculateAverageRating(Restaurant restaurant) {
        List<ReservationReview> reservationReviews = reservationReviewRepository.findAllByRestaurantId(
            restaurant.getId());
        List<WaitingReview> waitingReviews = waitingReviewRepository.findAllByRestaurantId(
            restaurant.getId());

        double totalRating =
            reservationReviews.stream().mapToDouble(ReservationReview::getRating).sum() +
                waitingReviews.stream().mapToDouble(WaitingReview::getRating).sum();

        int totalReviews = reservationReviews.size() + waitingReviews.size();

        return totalReviews > 0 ? totalRating / totalReviews : 0;
    }
}