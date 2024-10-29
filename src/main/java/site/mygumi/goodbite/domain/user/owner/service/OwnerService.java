package site.mygumi.goodbite.domain.user.owner.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.common.external.s3.service.S3Service;
import site.mygumi.goodbite.domain.menu.entity.Menu;
import site.mygumi.goodbite.domain.menu.repository.MenuRepository;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.domain.review.entity.WaitingReview;
import site.mygumi.goodbite.domain.review.repository.ReservationReviewRepository;
import site.mygumi.goodbite.domain.review.repository.WaitingReviewRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.exception.UserErrorCode;
import site.mygumi.goodbite.domain.user.exception.detail.PasswordMismatchException;
import site.mygumi.goodbite.domain.user.exception.detail.SamePasswordException;
import site.mygumi.goodbite.domain.user.owner.dto.OwnerResponseDto;
import site.mygumi.goodbite.domain.user.owner.dto.OwnerSignUpRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateBusinessNumberRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerNicknameRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerPasswordRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerPhoneNumberRequestDto;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.entity.OwnerStatus;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.detail.InvalidBusinessNumberException;
import site.mygumi.goodbite.domain.user.owner.exception.detail.OwnerAlreadyDeletedException;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;

/**
 * 사업자 관련 서비스 클래스입니다.
 * <p>
 * 사업자 회원가입, 조회, 정보 수정(닉네임, 전화번호, 사업자번호, 비밀번호), 탈퇴 등을 담당하며, {@link OwnerRepository}를 통해 데이터베이스와
 * 상호작용하여 각 작업을 처리합니다.
 * </p>
 * <b>주요 기능:</b>
 * <ul>
 *   <li>사업자 회원가입 및 사업자번호 유효성 검사</li>
 *   <li>사업자 정보 조회 및 정보 수정</li>
 *   <li>사업자 탈퇴 및 관련 데이터 삭제</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusinessVerificationService businessVerificationService;
    private final RestaurantRepository restaurantRepository;
    private final OperatingHourRepository operatingHourRepository;
    private final MenuRepository menuRepository;
    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationReviewRepository reservationReviewRepository;
    private final WaitingReviewRepository waitingReviewRepository;
    private final S3Service s3Service;

    /**
     * 사업자 회원가입 메서드입니다.
     * <p>사업자 등록번호 유효성 검사와 중복 필드 검증을 거친 후, 암호화된 비밀번호로 회원 정보를 저장합니다.</p>
     *
     * @param requestDto 사업자 가입 요청 데이터를 담은 DTO
     * @throws InvalidBusinessNumberException 사업자 등록번호가 유효하지 않을 때 발생
     */
    @Transactional
    public void signup(OwnerSignUpRequestDto requestDto) {
        validateDuplicateFields(requestDto.getNickname(), requestDto.getEmail(),
            requestDto.getPhoneNumber(), requestDto.getBusinessNumber());

        // 사업자 등록번호 유효성 검사
        boolean isValidBusinessNumber = businessVerificationService.verifyBusinessNumber(
            requestDto.getBusinessNumber());
        if (!isValidBusinessNumber) {
            throw new InvalidBusinessNumberException(OwnerErrorCode.INVALID_BUSINESS_NUMBER);
        }

        // 비밀번호 암호화 -> 인증인가연결시 config에서 PasswordEncoder Bean등록
        String password = passwordEncoder.encode(requestDto.getPassword());

        // OwnerStatus 설정 -> isValidBusinessNumber값이 참이면 인증상태로 설정
        OwnerStatus ownerStatus =
            isValidBusinessNumber ? OwnerStatus.VERIFIED : OwnerStatus.UNVERIFIED;

        // User DB에 저장
        Owner owner = Owner.builder()
            .email(requestDto.getEmail())
            .nickname(requestDto.getNickname())
            .password(password)
            .phoneNumber(requestDto.getPhoneNumber())
            .businessNumber(requestDto.getBusinessNumber())
            .ownerStatus(ownerStatus)
            .build();

        ownerRepository.save(owner);
    }


    /**
     * 사업자 정보를 조회하는 메서드입니다.
     *
     * @param user 인증된 사용자 정보
     * @return 조회된 사업자 정보를 담은 {@link OwnerResponseDto}
     */
    @Transactional(readOnly = true)
    public OwnerResponseDto getOwner(UserCredentials user) {
        Owner owner = (Owner) user;
        return OwnerResponseDto.from(ownerRepository.findByIdOrThrow(owner.getId()));
    }

    /**
     * 닉네임을 수정하는 메서드입니다.
     *
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @param user       인증된 사용자 정보
     */
    @Transactional
    public void updateNickname(UpdateOwnerNicknameRequestDto requestDto,
        UserCredentials user) {
        String newNickname = requestDto.getNewNickname();
        ownerRepository.validateDuplicateNickname(newNickname); //중복닉네임확인
        Owner owner = (Owner) user;
        owner.updateNickname(newNickname);
        ownerRepository.save(owner);
    }

    /**
     * 전화번호를 수정하는 메서드입니다.
     *
     * @param requestDto 새로운 전화번호를 담은 DTO
     * @param user       인증된 사용자 정보
     */
    @Transactional
    public void updatePhoneNumber(UpdateOwnerPhoneNumberRequestDto requestDto,
        UserCredentials user) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();
        ownerRepository.validateDuplicatePhoneNumber(newPhoneNumber);
        Owner owner = (Owner) user;
        owner.updatePhoneNumber(newPhoneNumber);// 전화번호 업데이트
        ownerRepository.save(owner);
    }


    /**
     * 사업자 등록번호를 수정하는 메서드입니다.
     * <p>등록번호 유효성 검사와 중복 여부 검증을 통해 인증된 사업자 상태로 업데이트합니다.</p>
     *
     * @param requestDto 새로운 사업자 등록번호를 담은 DTO
     * @param user       인증된 사용자 정보
     * @throws InvalidBusinessNumberException 유효하지 않은 사업자 등록번호일 때 발생
     */
    @Transactional
    public void updateBusinessNumber(UpdateBusinessNumberRequestDto requestDto,
        UserCredentials user) {
        String newBusinessNumber = requestDto.getNewBusinessNumber();
        // UserCredential타입의 객체를 Owner타입으로 캐스팅
        Owner owner = (Owner) user;

        // 사업자 등록번호 유효성 검사
        boolean isValidBusinessNumber = businessVerificationService.verifyBusinessNumber(
            newBusinessNumber);
        if (!isValidBusinessNumber) {
            throw new InvalidBusinessNumberException(OwnerErrorCode.INVALID_BUSINESS_NUMBER);
        }

        ownerRepository.validateDuplicateBusinessNumber(requestDto.getNewBusinessNumber());

        // 사업자번호 업데이트
        owner.updateBusinessNumber(newBusinessNumber);

        // 사업자번호 인증상태 업데이트-> isValidBusinessNumber가 참이어야 인증상태로 설정
        owner.updateOwnerStatus(
            isValidBusinessNumber ? OwnerStatus.VERIFIED : OwnerStatus.UNVERIFIED);

        // 명시적으로 저장
        ownerRepository.save(owner);

    }

    /**
     * 비밀번호를 수정하는 메서드입니다.
     * <p>현재 비밀번호 확인 후 새 비밀번호를 암호화하여 저장합니다.</p>
     *
     * @param requestDto 현재 비밀번호와 새 비밀번호를 담은 DTO
     * @param user       인증된 사용자 정보
     * @throws PasswordMismatchException 현재 비밀번호가 일치하지 않는 경우 발생
     * @throws SamePasswordException     새 비밀번호가 기존 비밀번호와 동일한 경우 발생
     */
    @Transactional
    public void updatePassword(
        UpdateOwnerPasswordRequestDto requestDto, UserCredentials user) {
        Owner owner = (Owner) user;

        //입력한 비밀번호와 사용자의 비밀번호 일치유무 확인
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), owner.getPassword())) {
            throw new PasswordMismatchException(UserErrorCode.PASSWORD_MISMATCH);
        }

        //새로 입력한 비밀번호가 기존의 비밀번호와 일치하는지 확인
        if (passwordEncoder.matches(requestDto.getNewPassword(), owner.getPassword())) {
            throw new SamePasswordException(UserErrorCode.SAME_PASSWORD);
        }

        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // 비밀번호 업데이트
        owner.updatePassword(newPassword);

        //명시적저장
        ownerRepository.save(owner);
    }

    /**
     * 사업자 계정을 삭제하는 메서드입니다.
     * <p>계정의 소프트 삭제 처리 및 연관된 레스토랑과 관련 데이터를 모두 삭제합니다.</p>
     *
     * @param user 인증된 사용자 정보
     * @throws OwnerAlreadyDeletedException 이미 삭제된 사용자일 때 발생
     */
    @Transactional
    public void deleteOwner(UserCredentials user) {
        Owner owner = (Owner) user;
        // 이미 탈퇴한 사용자인지 확인
        if (owner.getDeletedAt() != null) {
            throw new OwnerAlreadyDeletedException(OwnerErrorCode.OWNER_ALREADY_DELETED);
        }

        // 소프트 삭제를 위해 deletedAt 필드를 현재 시간으로 설정
        owner.deactivate();
        ownerRepository.save(owner);

        //연관 레스토랑 삭제
        Optional<Restaurant> restaurantOptional = restaurantRepository.findByOwnerId(owner.getId());
        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();

            // 레스토랑 연관 영업시간 삭제
            List<OperatingHour> operatingHours = operatingHourRepository.findAllByRestaurantId(
                restaurant.getId());
            operatingHourRepository.deleteAll(operatingHours);

            // 레스토랑 연관 웨이팅 리뷰 삭제
            List<WaitingReview> waitingReviews = waitingReviewRepository.findAllByRestaurantId(
                restaurant.getId());
            waitingReviewRepository.deleteAll(waitingReviews);

            // 레스토랑 연관 예약 리뷰 삭제
            List<ReservationReview> reservationReviews = reservationReviewRepository.findAllByRestaurantId(
                restaurant.getId());
            reservationReviewRepository.deleteAll(reservationReviews);

            // 레스토랑 연관 웨이팅 삭제
            List<Waiting> waitings = waitingRepository.findAllByRestaurantId(restaurant.getId());
            waitingRepository.deleteAll(waitings);

            // 레스토랑 연관 예약 삭제
            List<Reservation> reservations = reservationRepository.findAllByRestaurantId(
                restaurant.getId());
            reservationRepository.deleteAll(reservations);

            // 레스토랑 연관 메뉴 삭제
            List<Menu> menus = menuRepository.findAllByRestaurantId(restaurant.getId());
            menuRepository.deleteAll(menus);

            restaurantRepository.delete(restaurant);
            s3Service.deleteImageFromS3(restaurant.getImageUrl());
        }
    }

    /**
     * 가입 시 닉네임, 이메일, 전화번호, 사업자번호 중복 여부를 검증하는 메서드입니다.
     *
     * @param nickname       닉네임
     * @param email          이메일
     * @param phoneNumber    전화번호
     * @param businessNumber 사업자번호
     */
    private void validateDuplicateFields(String nickname, String email, String phoneNumber,
        String businessNumber) {
        ownerRepository.validateDuplicateNickname(nickname);
        ownerRepository.validateDuplicateEmail(email);
        ownerRepository.validateDuplicatePhoneNumber(phoneNumber);
        ownerRepository.validateDuplicateBusinessNumber(businessNumber);
    }
}