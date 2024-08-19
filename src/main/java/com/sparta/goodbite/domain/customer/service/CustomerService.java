package com.sparta.goodbite.domain.customer.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.dto.CustomerResponseDto;
import com.sparta.goodbite.domain.customer.dto.CustomerSignupRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateNicknameRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePasswordRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePhoneNumberRequestDto;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.customer.repository.CustomerRepository;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.repository.WaitingRepository;
import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.detail.CustomerAlreadyDeletedException;
import com.sparta.goodbite.exception.user.UserErrorCode;
import com.sparta.goodbite.exception.user.detail.PasswordMismatchException;
import com.sparta.goodbite.exception.user.detail.SamePasswordException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final WaitingRepository waitingRepository;

    //조회
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomer(UserCredentials user) {
        Customer customer = (Customer) user;
        return CustomerResponseDto.from(customerRepository.findByIdOrThrow(customer.getId()));
    }

    //회원가입
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

    //수정-닉네임
    @Transactional
    public void updateNickname(UpdateNicknameRequestDto requestDto, UserCredentials user) {
        String newNickname = requestDto.getNewNickname();
        customerRepository.validateDuplicateNickname(newNickname); //중복 닉네임 확인
        Customer customer = (Customer) user;
        customer.updateNickname(newNickname);// 닉네임 업데이트
        customerRepository.save(customer);//명시적으로 저장
    }

    //수정-전화번호
    @Transactional
    public void updatePhoneNumber(UpdatePhoneNumberRequestDto requestDto, UserCredentials user) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();
        customerRepository.validateDuplicatePhoneNumber(newPhoneNumber);//중복 전화번호 확인
        Customer customer = (Customer) user;
        customer.updatePhoneNumber(newPhoneNumber);// 전화번호 업데이트
        customerRepository.save(customer);//명시적으로 저장
    }

    //수정-비밀번호
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

        //명시적으로 저장
        customerRepository.save(customer);
    }

    // 중복 필드 검증 메서드
    private void validateDuplicateFields(String nickname, String email, String phoneNumber) {
        customerRepository.validateDuplicateNickname(nickname);
        customerRepository.validateDuplicateEmail(email);
        customerRepository.validateDuplicatePhoneNumber(phoneNumber);
    }

}