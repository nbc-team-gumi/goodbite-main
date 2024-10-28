package site.mygumi.goodbite.domain.review.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.domain.review.entity.WaitingReview;
import site.mygumi.goodbite.domain.review.repository.ReservationReviewRepository;
import site.mygumi.goodbite.domain.review.repository.WaitingReviewRepository;

/**
 * 리뷰와 관련된 기능을 제공하는 서비스 클래스입니다. 예약 리뷰와 대기 리뷰의 통합 조회 및 식당 평점 업데이트 기능을 제공합니다.
 *
 * @author sillysillyman
 */
@RequiredArgsConstructor
@Service
public class TotalReviewService {

    private final ReservationReviewRepository reservationReviewRepository;
    private final WaitingReviewRepository waitingReviewRepository;
    private final RestaurantRepository restaurantRepository;

    /**
     * 모든 리뷰를 시간순으로 정렬하고 페이지네이션 처리하여 반환합니다.
     *
     * @param pageable           페이지 정보
     * @param reservationReviews 예약 리뷰 리스트
     * @param waitingReviews     대기 리뷰 리스트
     * @return 페이지네이션 처리된 리뷰 리스트
     */
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

    /**
     * 특정 식당의 평점을 업데이트합니다.
     *
     * @param restaurantId 평점을 업데이트할 식당의 ID
     */
    @Transactional
    public void updateRestaurantRating(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        restaurant.updateRating(calculateAverageRating(restaurant));
    }

    /**
     * 특정 식당의 리뷰 평점 평균을 계산합니다. 이 메서드는 동시성 처리를 위해 Lock이 필요할 수 있습니다.
     *
     * @param restaurant 평점을 계산할 식당 객체
     * @return 계산된 평균 평점
     */
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