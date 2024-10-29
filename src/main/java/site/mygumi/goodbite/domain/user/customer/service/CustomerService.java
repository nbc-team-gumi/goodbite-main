package site.mygumi.goodbite.domain.user.customer.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.repository.ReservationRepository;
import site.mygumi.goodbite.domain.user.customer.dto.CustomerResponseDto;
import site.mygumi.goodbite.domain.user.customer.dto.CustomerSignupRequestDto;
import site.mygumi.goodbite.domain.user.customer.dto.UpdateNicknameRequestDto;
import site.mygumi.goodbite.domain.user.customer.dto.UpdatePasswordRequestDto;
import site.mygumi.goodbite.domain.user.customer.dto.UpdatePhoneNumberRequestDto;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.detail.CustomerAlreadyDeletedException;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.exception.UserErrorCode;
import site.mygumi.goodbite.domain.user.exception.detail.PasswordMismatchException;
import site.mygumi.goodbite.domain.user.exception.detail.SamePasswordException;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.repository.WaitingRepository;

/**
 * 고객 관련 서비스 클래스입니다.
 * <p>
 * 이 클래스는 회원가입, 정보 조회, 정보 수정(닉네임, 전화번호, 비밀번호), 회원 탈퇴 등의 기능을 제공합니다. 주요 비즈니스 로직은
 * {@link CustomerRepository}를 통해 데이터베이스와 상호작용하여 처리됩니다.
 * </p>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 사용자 정보를 조회하는 메서드입니다.
     *
     * @param user 인증된 사용자 정보
     * @return 조회된 사용자 정보를 담은 {@link CustomerResponseDto}
     */
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomer(UserCredentials user) {
        Customer customer = (Customer) user;
        return CustomerResponseDto.from(customerRepository.findByIdOrThrow(customer.getId()));
    }

    /**
     * 회원가입 메서드입니다.
     * <p>닉네임, 이메일, 전화번호 중복 여부를 검증한 후 사용자의 정보를 저장합니다.</p>
     *
     * @param requestDto 회원가입 요청 데이터를 담은 DTO
     */
    @Transactional
    public void signup(CustomerSignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        String email = requestDto.getEmail();
        String phoneNumber = requestDto.getPhoneNumber();

        validateDuplicateFields(nickname, email, phoneNumber);

        // 비밀번호 암호화 -> 인증인가연결시 config에서 PasswordEncoder Bean등록
        String password = passwordEncoder.encode(requestDto.getPassword());

        // User DB에 저장
        Customer customer = Customer.builder()
            .email(email)
            .nickname(nickname)
            .password(password)
            .phoneNumber(phoneNumber)
            .build();

        customerRepository.save(customer);
    }

    /**
     * 닉네임을 수정하는 메서드입니다.
     *
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @param user       인증된 사용자 정보
     */
    @Transactional
    public void updateNickname(UpdateNicknameRequestDto requestDto, UserCredentials user) {
        String newNickname = requestDto.getNewNickname();
        customerRepository.validateDuplicateNickname(newNickname); //중복 닉네임 확인
        Customer customer = (Customer) user;
        customer.updateNickname(newNickname);// 닉네임 업데이트
        customerRepository.save(customer);//명시적으로 저장
    }

    /**
     * 전화번호를 수정하는 메서드입니다.
     *
     * @param requestDto 새로운 전화번호를 담은 DTO
     * @param user       인증된 사용자 정보
     */
    @Transactional
    public void updatePhoneNumber(UpdatePhoneNumberRequestDto requestDto, UserCredentials user) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();
        customerRepository.validateDuplicatePhoneNumber(newPhoneNumber);//중복 전화번호 확인
        Customer customer = (Customer) user;
        customer.updatePhoneNumber(newPhoneNumber);// 전화번호 업데이트
        customerRepository.save(customer);//명시적으로 저장
    }

    /**
     * 비밀번호를 수정하는 메서드입니다.
     * <p>현재 비밀번호 검증 후, 새 비밀번호가 기존 비밀번호와 동일하지 않은지 확인하여 업데이트합니다.</p>
     *
     * @param requestDto 현재 비밀번호와 새 비밀번호를 담은 DTO
     * @param user       인증된 사용자 정보
     * @throws PasswordMismatchException 현재 비밀번호가 일치하지 않는 경우 발생
     * @throws SamePasswordException     새 비밀번호가 기존 비밀번호와 동일한 경우 발생
     */
    @Transactional
    public void updatePassword(UpdatePasswordRequestDto requestDto, UserCredentials user) {
        Customer customer = (Customer) user;

        //입력한 비밀번호와 사용자의 비밀번호 일치유무 확인
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), customer.getPassword())) {
            throw new PasswordMismatchException(UserErrorCode.PASSWORD_MISMATCH);
        }

        //새로 입력한 비밀번호가 기존의 비밀번호와 일치하는지 확인
        if (passwordEncoder.matches(requestDto.getNewPassword(), customer.getPassword())) {
            throw new SamePasswordException(UserErrorCode.SAME_PASSWORD);
        }

        //새 비밀번호 암호화
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // 비밀번호 업데이트
        customer.updatePassword(newPassword);

        //명시적으로 저장
        customerRepository.save(customer);
    }

    /**
     * 회원 탈퇴 메서드입니다.
     * <p>탈퇴 시 사용자 계정을 소프트 삭제하고, 해당 사용자의 웨이팅 및 예약 정보를 삭제합니다.</p>
     *
     * @param user 인증된 사용자 정보
     * @throws CustomerAlreadyDeletedException 이미 탈퇴한 사용자인 경우 발생
     */
    @Transactional
    public void deleteCustomer(UserCredentials user) {
        Customer customer = (Customer) user;

        // 이미 탈퇴한 사용자인지 확인합니다.
        if (customer.getDeletedAt() != null) {
            throw new CustomerAlreadyDeletedException(CustomerErrorCode.CUSTOMER_ALREADY_DELETED);
        }

        // 소프트 삭제를 위해 deletedAt 필드를 현재 시간으로 설정
        customer.deactivate();

        //사용자의 웨이팅을 하드 딜리트 함
        List<Waiting> waitingList = waitingRepository.findALLByCustomerId(user.getId());
        waitingRepository.deleteAll(waitingList);

        List<Reservation> reservationList = reservationRepository.findAllByCustomerId(user.getId());
        reservationRepository.deleteAll(reservationList);

        //명시적으로 저장
        customerRepository.save(customer);
    }

    /**
     * 닉네임, 이메일, 전화번호 중복 여부를 검증하는 메서드입니다.
     *
     * @param nickname    닉네임
     * @param email       이메일
     * @param phoneNumber 전화번호
     */
    private void validateDuplicateFields(String nickname, String email, String phoneNumber) {
        customerRepository.validateDuplicateNickname(nickname);
        customerRepository.validateDuplicateEmail(email);
        customerRepository.validateDuplicatePhoneNumber(phoneNumber);
    }

}