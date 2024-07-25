package com.sparta.goodbite.domain.customer.service;

import com.sparta.goodbite.domain.customer.dto.CustomerResponseDto;
import com.sparta.goodbite.domain.customer.dto.CustomerSignUpRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateNicknameRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePasswordRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePhoneNumberRequestDto;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.customer.repository.CustomerRepository;
import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.detail.CustomerAlreadyDeletedException;
import com.sparta.goodbite.exception.customer.detail.CustomerNotFoundException;
import com.sparta.goodbite.exception.customer.detail.DuplicateEmailException;
import com.sparta.goodbite.exception.customer.detail.DuplicateNicknameException;
import com.sparta.goodbite.exception.customer.detail.DuplicatePhoneNumberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomer(Long customerId) {
        return CustomerResponseDto.from(customerRepository.findById(customerId).orElseThrow(()
            -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND)));
    }

    @Transactional
    public void signUp(CustomerSignUpRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        String email = requestDto.getEmail();
        String phoneNumber = requestDto.getPhoneNumber();

        // 닉네임 중복 검사
        customerRepository.findByNickname(nickname).ifPresent(unused -> {
            throw new DuplicateNicknameException(CustomerErrorCode.DUPLICATE_NICKNAME);
        });
        // 이메일 중복 검사
        customerRepository.findByEmail(email).ifPresent(unused -> {
            throw new DuplicateEmailException(CustomerErrorCode.DUPLICATE_EMAIL);
        });
        // 전화번호 중복 검사
        customerRepository.findByPhoneNumber(phoneNumber).ifPresent(unused -> {
            throw new DuplicatePhoneNumberException(CustomerErrorCode.DUPLICATE_PHONE_NUMBER);
        });

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

    @Transactional
    public void updateNickname(Long customerId, UpdateNicknameRequestDto requestDto) {
        String newNickname = requestDto.getNewNickname();

        // 닉네임 중복 검사
        customerRepository.findByNickname(newNickname).ifPresent(unused -> {
            throw new DuplicateNicknameException(CustomerErrorCode.DUPLICATE_NICKNAME);
        });

        // Customer 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        // 닉네임 업데이트
        customer.updateNickname(newNickname);
    }

    @Transactional
    public void updatePhoneNumber(Long customerId, UpdatePhoneNumberRequestDto requestDto) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();

        // 전화번호 중복 검사
        customerRepository.findByPhoneNumber(newPhoneNumber).ifPresent(unused -> {
            throw new DuplicatePhoneNumberException(CustomerErrorCode.DUPLICATE_PHONE_NUMBER);
        });

        // Customer 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        // 전화번호 업데이트
        customer.updatePhoneNumber(newPhoneNumber);
    }

    @Transactional
    public void updatePassword(Long customerId, UpdatePasswordRequestDto requestDto
        /*,Customer customer*/) {
        /*//현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), customer.getPassword())) {
            throw new PasswordMismatchException(UserErrorCode.INVALID_CURRENT_PASSWORD);
        }

        //새 비밀번호와 기존 비밀번호 동일여부 확인
        if (passwordEncoder.matches(requestDto.getNewPassword(), customer.getPassword())) {
            throw new SamePasswordException(UserErrorCode.PASSWORD_SAME_AS_OLD);
        }*/

        // Customer 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        //새 비밀번호 암호화
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // 비밀번호 업데이트
        customer.updatePassword(newPassword);

    }

    @Transactional
    public void deleteCustomer(Long customerId) {
        // 고객 엔티티 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        // 이미 탈퇴한 사용자인지 확인합니다.
        if (customer.getDeletedAt() != null) {
            throw new CustomerAlreadyDeletedException(CustomerErrorCode.CUSTOMER_ALREADY_DELETED);
        }

        // 소프트 삭제를 위해 deletedAt 필드를 현재 시간으로 설정
        customer.deactivate();
    }

}
