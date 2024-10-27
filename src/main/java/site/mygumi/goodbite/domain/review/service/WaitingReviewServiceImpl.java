package site.mygumi.goodbite.domain.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.dto.CreateWaitingReviewRequestDto;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.dto.UpdateReviewRequestDto;
import site.mygumi.goodbite.domain.review.entity.WaitingReview;
import site.mygumi.goodbite.domain.review.repository.WaitingReviewRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;
import site.mygumi.goodbite.exception.review.ReviewErrorCode;
import site.mygumi.goodbite.exception.review.detail.CanNotSubmitReviewException;

/**
 * 대기 리뷰 관련 비즈니스 로직을 처리하는 서비스 구현 클래스입니다. 리뷰 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @param <CreateWaitingReviewRequestDto> 대기 리뷰 생성 요청 DTO 타입
 * @param <UpdateReviewRequestDto>        리뷰 수정 요청 DTO 타입
 * @author sillysillyman
 */
@RequiredArgsConstructor
@Service
public class WaitingReviewServiceImpl implements
    ReviewService<CreateWaitingReviewRequestDto, UpdateReviewRequestDto> {

    private final RestaurantRepository restaurantRepository;
    private final WaitingRepository waitingRepository;
    private final WaitingReviewRepository waitingReviewRepository;
    private final TotalReviewService totalReviewService;

    /**
     * 새로운 대기 리뷰를 생성합니다.
     *
     * @param createWaitingReviewRequestDto 대기 리뷰 생성 요청 정보가 담긴 DTO
     * @param user                          리뷰를 생성하는 사용자의 인증 정보
     * @throws CanNotSubmitReviewException 리뷰를 제출할 수 없는 경우 발생합니다.
     * @throws AuthException               사용자가 리뷰 작성 권한이 없는 경우 발생합니다.
     */
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

    /**
     * 특정 ID의 리뷰를 조회합니다.
     *
     * @param reviewId 조회할 리뷰의 ID
     * @return 조회된 리뷰 정보가 담긴 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(waitingReviewRepository.findByIdOrThrow(reviewId));
    }

    /**
     * 모든 대기 리뷰를 조회합니다.
     *
     * @return 모든 대기 리뷰를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return waitingReviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    /**
     * 특정 레스토랑에 대한 모든 대기 리뷰를 조회합니다.
     *
     * @param restaurantId 레스토랑의 ID
     * @return 해당 레스토랑의 모든 대기 리뷰를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId) {
        return waitingReviewRepository.findAllByRestaurantId(restaurantId).stream()
            .map(ReviewResponseDto::from).toList();
    }

    /**
     * 사용자가 작성한 모든 대기 리뷰를 조회합니다.
     *
     * @param user 현재 사용자의 인증 정보
     * @return 사용자가 작성한 대기 리뷰를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getMyReviews(UserCredentials user) {
        return waitingReviewRepository.findAllByCustomerId(user.getId()).stream()
            .map(ReviewResponseDto::from).toList();
    }

    /**
     * 특정 ID의 대기 리뷰를 업데이트합니다.
     *
     * @param reviewId               업데이트할 대기 리뷰의 ID
     * @param updateReviewRequestDto 대기 리뷰 업데이트 요청 정보가 담긴 DTO
     * @param user                   대기 리뷰를 업데이트하는 사용자의 인증 정보
     */
    @Override
    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        WaitingReview review = findReviewByIdAndValidateCustomer(reviewId, user.getId());
        review.update(updateReviewRequestDto);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    /**
     * 특정 ID의 대기 리뷰를 삭제합니다.
     *
     * @param reviewId 삭제할 대기 리뷰의 ID
     * @param user     대기 리뷰를 삭제하는 사용자의 인증 정보
     */
    @Override
    @Transactional
    public void deleteReview(Long reviewId, UserCredentials user) {
        WaitingReview review = findReviewByIdAndValidateCustomer(reviewId, user.getId());
        waitingReviewRepository.delete(review);
        totalReviewService.updateRestaurantRating(review.getRestaurant().getId());
    }

    /**
     * 특정 ID의 대기 리뷰를 조회하고, 해당 리뷰가 사용자에 의해 작성되었는지 검증합니다.
     *
     * @param reviewId   검증할 대기 리뷰의 ID
     * @param customerId 대기 리뷰 작성자의 ID
     * @return 조회된 대기 리뷰
     * @throws AuthException 대기 리뷰 작성자가 아닌 경우 발생합니다.
     */
    private WaitingReview findReviewByIdAndValidateCustomer(Long reviewId, Long customerId) {
        WaitingReview waitingReview = waitingReviewRepository.findByIdOrThrow(reviewId);

        if (!waitingReview.getCustomer().getId().equals(customerId)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return waitingReview;
    }
}