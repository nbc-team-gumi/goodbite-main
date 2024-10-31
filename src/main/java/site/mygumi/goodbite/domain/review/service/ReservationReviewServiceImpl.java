package site.mygumi.goodbite.domain.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.dto.CreateReservationReviewRequestDto;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.dto.UpdateReviewRequestDto;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.domain.review.exception.ReviewErrorCode;
import site.mygumi.goodbite.domain.review.exception.detail.CanNotSubmitReviewException;
import site.mygumi.goodbite.domain.review.repository.ReservationReviewRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;

/**
 * 예약 리뷰 관련 비즈니스 로직을 처리하는 서비스 구현 클래스입니다. 리뷰 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @param <CreateReservationReviewRequestDto> 리뷰 생성 요청 DTO 타입
 * @param <UpdateReviewRequestDto>            리뷰 수정 요청 DTO 타입
 * @author sillysillyman
 */
@RequiredArgsConstructor
@Service
public class ReservationReviewServiceImpl implements
    ReviewService<CreateReservationReviewRequestDto, UpdateReviewRequestDto> {

    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationReviewRepository reservationReviewRepository;
    private final TotalReviewService totalReviewService;

    /**
     * 새로운 예약 리뷰를 생성합니다.
     *
     * @param createReservationReviewRequestDto 리뷰 생성 요청 정보가 담긴 DTO
     * @param user                              리뷰를 생성하는 사용자의 인증 정보
     * @throws CanNotSubmitReviewException 리뷰를 제출할 수 없는 경우 발생합니다.
     * @throws AuthException               사용자가 리뷰 작성 권한이 없는 경우 발생합니다.
     */
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
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createReservationReviewRequestDto.getRestaurantId());
        reservationReviewRepository.save(
            createReservationReviewRequestDto.toEntity(restaurant, (Customer) user, reservation));
        totalReviewService.updateRestaurantRating(restaurant.getId());
    }

    /**
     * 특정 ID의 리뷰를 조회합니다.
     *
     * @param reviewId 조회할 리뷰의 ID
     * @return 조회된 리뷰 정보가 담긴 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(reservationReviewRepository.findByIdOrThrow(reviewId));
    }

    /**
     * 모든 리뷰를 조회합니다.
     *
     * @return 모든 리뷰를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reservationReviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    /**
     * 특정 레스토랑에 대한 모든 리뷰를 조회합니다.
     *
     * @param restaurantId 레스토랑의 ID
     * @return 해당 레스토랑의 모든 리뷰를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId) {
        return reservationReviewRepository.findAllByRestaurantId(restaurantId).stream()
            .map(ReviewResponseDto::from).toList();
    }

    /**
     * 사용자가 작성한 모든 리뷰를 조회합니다.
     *
     * @param user 현재 사용자의 인증 정보
     * @return 사용자가 작성한 리뷰를 담은 DTO 리스트
     */
    @Override
    public List<ReviewResponseDto> getMyReviews(UserCredentials user) {
        return reservationReviewRepository.findAllByCustomerId(user.getId()).stream()
            .map(ReviewResponseDto::from).toList();
    }

    /**
     * 특정 ID의 리뷰를 업데이트합니다.
     *
     * @param reviewId               업데이트할 리뷰의 ID
     * @param updateReviewRequestDto 리뷰 업데이트 요청 정보가 담긴 DTO
     * @param user                   리뷰를 업데이트하는 사용자의 인증 정보
     */
    @Override
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        ReservationReview review = getReviewByIdAndValidateCustomer(reviewId, user.getId());
        review.update(updateReviewRequestDto);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    /**
     * 특정 ID의 리뷰를 삭제합니다.
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param user     리뷰를 삭제하는 사용자의 인증 정보
     */
    @Override
    public void deleteReview(Long reviewId, UserCredentials user) {
        ReservationReview review = getReviewByIdAndValidateCustomer(reviewId,
            user.getId());
        reservationReviewRepository.delete(review);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    /**
     * 특정 ID의 리뷰를 조회하고, 해당 리뷰가 사용자에 의해 작성되었는지 검증합니다.
     *
     * @param reviewId   검증할 리뷰의 ID
     * @param customerId 리뷰 작성자의 ID
     * @return 조회된 리뷰
     * @throws AuthException 리뷰 작성자가 아닌 경우 발생합니다.
     */
    private ReservationReview getReviewByIdAndValidateCustomer(Long reviewId, Long customerId) {
        ReservationReview reservationReview = reservationReviewRepository.findByIdOrThrow(reviewId);

        if (!reservationReview.getCustomer().getId().equals(customerId)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return reservationReview;
    }
}