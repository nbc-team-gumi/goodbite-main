package site.mygumi.goodbite.domain.waiting.service;

import java.util.List;
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
import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;
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
import site.mygumi.goodbite.domain.waiting.exception.WaitingException;
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

        return WaitingResponseDto.from(waiting);
    }

    /**
     * 특정 ID의 대기 정보를 조회합니다.
     *
     * @param waitingId 조회할 대기의 ID
     * @param user      대기 정보를 조회하는 사용자의 인증 정보
     * @return 조회된 대기 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public WaitingResponseDto getWaiting(Long waitingId, UserCredentials user) {

        validateWaitingRequest(user, waitingId);

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);
        return WaitingResponseDto.from(waiting);
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
    public WaitingResponseDto updateWaiting(Long waitingId,
        UpdateWaitingRequestDto updateWaitingRequestDto, UserCredentials user) {

        validateWaitingRequest(user, waitingId);

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

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
    public Page<WaitingResponseDto> getWaitingsByRestaurantId(Long restaurantId,
        UserCredentials user, Pageable pageable) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findById(restaurant.getOwner().getId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));

        // api 요청한 유저가 해당 레스토랑의 '오너'와 같지 않다면
        if (!user.getEmail().equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Page<Waiting> waitingPage = waitingRepository.findPageByRestaurantId(restaurantId,
            pageable);

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

        Page<Waiting> waitingPage = waitingRepository.findPageByCustomerId(user.getId(),
            sortedPageable);

        List<WaitingResponseDto> waitingResponseDtos = waitingPage.stream()
            .map(WaitingResponseDto::from).toList();
        return new PageImpl<>(waitingResponseDtos, sortedPageable, waitingPage.getTotalElements());
    }

    /**
     * 특정 대기에 대한 요청이 유효한지 확인합니다. 요청하는 사용자가 해당 대기의 손님이거나 해당 대기가 속한 레스토랑의 소유자인지 검증합니다.
     *
     * @param user      요청하는 사용자의 인증 정보
     * @param waitingId 검증할 대기의 ID
     * @throws AuthException     사용자가 해당 작업에 권한이 없는 경우 발생합니다.
     * @throws WaitingException  해당 레스토랑에 유효한 대기가 존재하지 않을 경우 발생합니다.
     * @throws CustomerException 대기 등록한 손님 정보가 존재하지 않을 경우 발생합니다.
     */
    private void validateWaitingRequest(UserCredentials user, Long waitingId) {

        Waiting waiting = waitingRepository.findNotDeletedByIdOrThrow(waitingId);

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            waiting.getRestaurant().getId());

        Customer customer = customerRepository.findById(waiting.getCustomer().getId())
            .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        Owner owner = ownerRepository.findById(restaurant.getOwner().getId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));

        // api 요청한 유저가 해당 레스토랑의 '오너'와 같던가 혹은 웨이팅 등록한 '손님'과 같던가
        if (user.getClass().equals(Owner.class) && !user.getEmail()
            .equals(owner.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        if (user.getClass().equals(Customer.class) && !user.getEmail()
            .equals(customer.getEmail())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        List<Waiting> waitingList = waitingRepository.findAllByRestaurantIdDeletedAtIsNull(
            restaurant.getId());
        if (waitingList.isEmpty()) {
            throw new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND);
        }
    }
}
