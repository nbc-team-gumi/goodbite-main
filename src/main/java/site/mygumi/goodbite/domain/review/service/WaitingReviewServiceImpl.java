package site.mygumi.goodbite.domain.review.service;

import site.mygumi.goodbite.common.UserCredentials;
import site.mygumi.goodbite.domain.customer.entity.Customer;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.dto.CreateWaitingReviewRequestDto;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.dto.UpdateReviewRequestDto;
import site.mygumi.goodbite.domain.review.entity.WaitingReview;
import site.mygumi.goodbite.domain.review.repository.WaitingReviewRepository;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;
import site.mygumi.goodbite.exception.review.ReviewErrorCode;
import site.mygumi.goodbite.exception.review.detail.CanNotSubmitReviewException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WaitingReviewServiceImpl implements
    ReviewService<CreateWaitingReviewRequestDto, UpdateReviewRequestDto> {

    private final RestaurantRepository restaurantRepository;
    private final WaitingRepository waitingRepository;
    private final WaitingReviewRepository waitingReviewRepository;
    private final TotalReviewService totalReviewService;

    @Override
    @Transactional
    public void createReview(CreateWaitingReviewRequestDto createWaitingReviewRequestDto,
        UserCredentials user) {

        Waiting waiting = waitingRepository.findByIdOrThrow(
            createWaitingReviewRequestDto.getWaitingId());

        if (!waiting.getCustomer().getId().equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        if (!waiting.canSubmitReview()) {
            throw new CanNotSubmitReviewException(ReviewErrorCode.CANNOT_SUBMIT_REVIEW);
        }

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createWaitingReviewRequestDto.getRestaurantId());
        waitingReviewRepository.save(
            createWaitingReviewRequestDto.toEntity(restaurant, (Customer) user, waiting));
        totalReviewService.updateRestaurantRating(restaurant.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(waitingReviewRepository.findByIdOrThrow(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return waitingReviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId) {
        return waitingReviewRepository.findAllByRestaurantId(restaurantId).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getMyReviews(UserCredentials user) {
        return waitingReviewRepository.findAllByCustomerId(user.getId()).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        WaitingReview review = findReviewByIdAndValidateCustomer(reviewId, user.getId());
        review.update(updateReviewRequestDto);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, UserCredentials user) {
        WaitingReview review = findReviewByIdAndValidateCustomer(reviewId, user.getId());
        waitingReviewRepository.delete(review);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    private WaitingReview findReviewByIdAndValidateCustomer(Long reviewId, Long customerId) {
        WaitingReview waitingReview = waitingReviewRepository.findByIdOrThrow(reviewId);

        if (!waitingReview.getCustomer().getId().equals(customerId)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return waitingReview;
    }
}