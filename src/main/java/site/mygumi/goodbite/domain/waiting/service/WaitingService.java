package site.mygumi.goodbite.domain.waiting.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.common.aspect.lock.RedisLock;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import site.mygumi.goodbite.domain.waiting.dto.CreateWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.WaitingResponseDto;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import site.mygumi.goodbite.domain.waiting.exception.WaitingErrorCode;
import site.mygumi.goodbite.domain.waiting.exception.detail.WaitingNotFoundException;
import site.mygumi.goodbite.domain.waiting.repository.WaitingOrderRepository;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;

/**
 * 대기 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 대기 생성, 조회, 수정, 삭제 등 대기 상태와 관련된 다양한 기능을 제공합니다.
 *
 * @author sillysillyman
 */
@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final WaitingOrderRepository waitingOrderRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final WaitingDailyCounterService waitingDailyCounterService;

    /**
     * 새로운 대기 요청을 생성합니다.
     *
     * @param createWaitingRequestDto 대기 요청 정보를 담은 DTO
     * @param user                    대기 요청을 생성하는 사용자의 인증 정보
     * @return 생성된 대기 정보를 담은 DTO
     */
    @RedisLock(key = "createWaitingLock")
    @Transactional
    public WaitingResponseDto createWaiting(
        CreateWaitingRequestDto createWaitingRequestDto,
        UserCredentials user
    ) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createWaitingRequestDto.getRestaurantId()
        );

        Customer customer = customerRepository.findByIdOrThrow(user.getId());

        waitingRepository.validateNoDuplicateWaiting(restaurant.getId(), customer.getId());

        Integer waitingNumber = waitingDailyCounterService.issueWaitingNumber(restaurant.getId());

        Waiting waiting = Waiting.builder()
            .restaurant(restaurant)
            .customer(customer)
            .waitingNumber(waitingNumber)
            .status(WaitingStatus.WAITING)
            .partySize(createWaitingRequestDto.getPartySize())
            .demand(createWaitingRequestDto.getDemand())
            .build();

        waitingRepository.save(waiting);
        waitingOrderRepository.addWaiting(
            restaurant.getId(),
            waiting.getId(),
            waiting.getWaitingNumber()
        );

        return WaitingResponseDto.from(waiting);
    }

    /**
     * 고객이 특정 ID의 대기 정보를 조회합니다.
     *
     * @param waitingId 조회할 대기의 ID
     * @param user      대기 정보를 조회하는 사용자의 인증 정보
     * @return 조회된 대기 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public WaitingResponseDto customerGetWaiting(Long waitingId, UserCredentials user) {
        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);
        validateWaitingOwnership(waiting, user);
        return WaitingResponseDto.from(waiting);
    }

    /**
     * 식당 주인이 특정 ID의 대기 정보를 조회합니다.
     *
     * @param waitingId 조회할 대기의 ID
     * @param user      대기 정보를 조회하는 사용자의 인증 정보
     * @return 조회된 대기 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public WaitingResponseDto ownerGetWaiting(Long waitingId, UserCredentials user) {
        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);
        validateRestaurantOwnership(waiting.getRestaurant(), user);
        return WaitingResponseDto.from(waiting);
    }

    /**
     * 특정 웨이팅의 현재 순서를 조회합니다.
     *
     * @param waitingId 조회할 웨이팅 ID
     * @param user      요청하는 사용자의 인증 정보
     * @return 현재 웨이팅 순서 (1부터 시작)
     */
    @Transactional(readOnly = true)
    public int getWaitingOrder(Long waitingId, UserCredentials user) {
        Waiting waiting = waitingRepository.findByIdOrThrow(waitingId);

        validateWaitingOwnership(waiting, user);

        Integer order = waitingOrderRepository.getWaitingOrder(
            waiting.getRestaurant().getId(),
            waitingId
        );

        if (order == null) {
            throw new WaitingNotFoundException(WaitingErrorCode.WAITING_NOT_FOUND);
        }

        // Redis는 0부터 시작하므로 1을 더해서 반환
        return order + 1;
    }

    /**
     * 특정 대기 요청을 업데이트합니다.
     *
     * @param user                    요청하는 사용자의 인증 정보
     * @param waitingId               업데이트할 대기의 ID
     * @param updateWaitingRequestDto 대기 요청 업데이트 정보를 담은 DTO
     * @return 업데이트된 대기 정보를 담은 DTO
     */
    @Transactional
    public WaitingResponseDto updateWaiting(
        Long waitingId,
        UpdateWaitingRequestDto updateWaitingRequestDto,
        UserCredentials user
    ) {

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        validateWaitingOwnership(waiting, user);

        waiting.update(updateWaitingRequestDto.getPartySize(), updateWaitingRequestDto.getDemand());

        waitingRepository.save(waiting);
        return WaitingResponseDto.from(waiting);
    }

    /**
     * 특정 레스토랑의 대기 요청을 페이지네이션하여 조회합니다.
     *
     * @param user         요청하는 사용자의 인증 정보
     * @param restaurantId 레스토랑의 ID
     * @param pageable     페이지 정보
     * @return 페이지네이션된 대기 정보
     */
    @Transactional(readOnly = true)
    public Page<WaitingResponseDto> getWaitingsByRestaurantId(
        Long restaurantId,
        UserCredentials user,
        Pageable pageable
    ) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findByIdOrThrow(restaurant.getOwner().getId());

        // api 요청한 유저가 해당 레스토랑의 '오너'와 같지 않다면
        if (!user.getEmail().equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Page<Waiting> waitingPage = waitingRepository.findPageByRestaurantId(
            restaurantId,
            pageable
        );

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(WaitingResponseDto::from).toList();

        return new PageImpl<>(waitingResponseDtos, pageable, waitingPage.getTotalElements());
    }

    /**
     * 사용자가 작성한 대기 요청을 페이지네이션하여 조회합니다.
     *
     * @param user     요청하는 사용자의 인증 정보
     * @param pageable 페이지 정보
     * @return 페이지네이션된 대기 정보
     */
    @Transactional(readOnly = true)
    public Page<WaitingResponseDto> getMyWaitings(UserCredentials user, Pageable pageable) {
        // 기존 pageable에 최신순 정렬을 추가
        Pageable sortedPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")  // 최신순으로 정렬
        );

        Page<Waiting> waitingPage = waitingRepository.findPageByCustomerId(
            user.getId(),
            sortedPageable
        );

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(WaitingResponseDto::from).toList();
        return new PageImpl<>(waitingResponseDtos, sortedPageable, waitingPage.getTotalElements());
    }

    @Transactional
    public void enterWaiting(Long waitingId, UserCredentials user) {
        Waiting waiting = waitingRepository.findByIdOrThrow(waitingId);

        validateRestaurantOwnership(waiting.getRestaurant(), user);

        waiting.enter();

        waitingOrderRepository.removeWaiting(waiting.getRestaurant().getId(), waitingId);
    }

    @Transactional
    public void noShowWaiting(Long waitingId, UserCredentials user) {
        Waiting waiting = waitingRepository.findByIdOrThrow(waitingId);

        validateRestaurantOwnership(waiting.getRestaurant(), user);

        waiting.noShow();

        waitingOrderRepository.removeWaiting(waiting.getRestaurant().getId(), waitingId);
    }

    @Transactional
    public void cancelWaiting(Long waitingId, UserCredentials user) {
        Waiting waiting = waitingRepository.findByIdOrThrow(waitingId);

        validateWaitingOwnership(waiting, user);

        waiting.cancel();

        waitingOrderRepository.removeWaiting(waiting.getRestaurant().getId(), waitingId);
    }

    private void validateWaitingOwnership(Waiting waiting, UserCredentials customer) {
        if (!(customer instanceof Customer)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        if (!Objects.equals(waiting.getCustomer().getId(), customer.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }

    private void validateRestaurantOwnership(Restaurant restaurant, UserCredentials owner) {
        if (!(owner instanceof Owner)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        if (!Objects.equals(restaurant.getOwner().getId(), owner.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}
