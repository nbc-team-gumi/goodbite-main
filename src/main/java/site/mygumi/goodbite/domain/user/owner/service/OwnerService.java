package site.mygumi.goodbite.domain.user.owner.service;

import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.common.s3.service.S3Service;
import site.mygumi.goodbite.domain.menu.entity.Menu;
import site.mygumi.goodbite.domain.menu.repository.MenuRepository;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import site.mygumi.goodbite.domain.user.owner.dto.OwnerResponseDto;
import site.mygumi.goodbite.domain.user.owner.dto.OwnerSignUpRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateBusinessNumberRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerNicknameRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerPasswordRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerPhoneNumberRequestDto;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.entity.OwnerStatus;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.review.entity.ReservationReview;
import site.mygumi.goodbite.domain.review.entity.WaitingReview;
import site.mygumi.goodbite.domain.review.repository.ReservationReviewRepository;
import site.mygumi.goodbite.domain.review.repository.WaitingReviewRepository;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;
import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.detail.InvalidBusinessNumberException;
import site.mygumi.goodbite.exception.owner.detail.OwnerAlreadyDeletedException;
import site.mygumi.goodbite.exception.user.UserErrorCode;
import site.mygumi.goodbite.exception.user.detail.PasswordMismatchException;
import site.mygumi.goodbite.exception.user.detail.SamePasswordException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //가입
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


    //조회
    @Transactional(readOnly = true)
    public OwnerResponseDto getOwner(UserCredentials user) {
        Owner owner = (Owner) user;
        return OwnerResponseDto.from(ownerRepository.findByIdOrThrow(owner.getId()));
    }

    // 수정-닉네임
    @Transactional
    public void updateNickname(UpdateOwnerNicknameRequestDto requestDto,
        UserCredentials user) {
        String newNickname = requestDto.getNewNickname();
        ownerRepository.validateDuplicateNickname(newNickname); //중복닉네임확인
        Owner owner = (Owner) user;
        owner.updateNickname(newNickname);
        ownerRepository.save(owner);
    }

    //수정-전화번호
    @Transactional
    public void updatePhoneNumber(UpdateOwnerPhoneNumberRequestDto requestDto,
        UserCredentials user) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();
        ownerRepository.validateDuplicatePhoneNumber(newPhoneNumber);
        Owner owner = (Owner) user;
        owner.updatePhoneNumber(newPhoneNumber);// 전화번호 업데이트
        ownerRepository.save(owner);
    }


    // 수정-사업자번호
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

    // 수정-비밀번호
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

    //삭제
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

    // 중복 필드 검증 메서드
    private void validateDuplicateFields(String nickname, String email, String phoneNumber,
        String businessNumber) {
        ownerRepository.validateDuplicateNickname(nickname);
        ownerRepository.validateDuplicateEmail(email);
        ownerRepository.validateDuplicatePhoneNumber(phoneNumber);
        ownerRepository.validateDuplicateBusinessNumber(businessNumber);
    }
}