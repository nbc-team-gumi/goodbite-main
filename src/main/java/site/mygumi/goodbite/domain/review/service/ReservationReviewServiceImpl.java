package site.mygumi.goodbite.domain.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.dto.CreateReservationReviewRequestDto;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.dto.UpdateReviewRequestDto;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.domain.review.repository.ReservationReviewRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.detail.UnauthorizedException;
import site.mygumi.goodbite.exception.review.ReviewErrorCode;
import site.mygumi.goodbite.exception.review.detail.CanNotSubmitReviewException;

@RequiredArgsConstructor
@Service
public class ReservationReviewServiceImpl implements
    ReviewService<CreateReservationReviewRequestDto, UpdateReviewRequestDto> {

    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationReviewRepository reservationReviewRepository;
    private final TotalReviewService totalReviewService;

    @Override
    @Transactional
    public void createReview(CreateReservationReviewRequestDto createReservationReviewRequestDto,
        UserCredentials user) {

        Reservation reservation = reservationRepository.findByIdOrThrow(
            createReservationReviewRequestDto.getReservationId());

        if (!reservation.canSubmitReview()) {
            throw new CanNotSubmitReviewException(ReviewErrorCode.CANNOT_SUBMIT_REVIEW);
        }

        if (!reservation.getCustomer().getId().equals(user.getId())) {
            throw new UnauthorizedException(AuthErrorCode.UNAUTHORIZED);
        }

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createReservationReviewRequestDto.getRestaurantId());
        reservationReviewRepository.save(
            createReservationReviewRequestDto.toEntity(restaurant, (Customer) user, reservation));
        totalReviewService.updateRestaurantRating(restaurant.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(reservationReviewRepository.findByIdOrThrow(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reservationReviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId) {
        return reservationReviewRepository.findAllByRestaurantId(restaurantId).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    public List<ReviewResponseDto> getMyReviews(UserCredentials user) {
        return reservationReviewRepository.findAllByCustomerId(user.getId()).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        ReservationReview review = getReviewByIdAndValidateCustomer(reviewId, user.getId());
        review.update(updateReviewRequestDto);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    @Override
    public void deleteReview(Long reviewId, UserCredentials user) {
        ReservationReview review = getReviewByIdAndValidateCustomer(reviewId,
            user.getId());
        reservationReviewRepository.delete(review);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    private ReservationReview getReviewByIdAndValidateCustomer(Long reviewId, Long customerId) {
        ReservationReview reservationReview = reservationReviewRepository.findByIdOrThrow(reviewId);

        if (!reservationReview.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException(AuthErrorCode.UNAUTHORIZED);
        }

        return reservationReview;
    }
}