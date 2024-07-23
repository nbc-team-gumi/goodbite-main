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
import java.time.LocalDateTime;
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
        customerRepository.findByNickname(nickname).ifPresent(u -> {
            throw new DuplicateNicknameException(CustomerErrorCode.DUPLICATE_NICKNAME);
        });
        // 이메일 중복 검사
        customerRepository.findByEmail(email).ifPresent(u -> {
            throw new DuplicateEmailException(CustomerErrorCode.DUPLICATE_EMAIL);
        });
        // 전화번호 중복 검사
        customerRepository.findByPhoneNumber(phoneNumber).ifPresent(u -> {
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
        customerRepository.findByNickname(newNickname).ifPresent(u -> {
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
        String newNewTelNo = requestDto.getNewPhonenumber();

        // 전화번호 중복 검사
        customerRepository.findByPhoneNumber(newNewTelNo).ifPresent(u -> {
            throw new DuplicatePhoneNumberException(CustomerErrorCode.DUPLICATE_PHONE_NUMBER);
        });

        // Customer 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        // 전화번호 업데이트
        customer.updatePhoneNumber(newNewTelNo);
    }

    @Transactional
    public void updatePassword(Long customerId, UpdatePasswordRequestDto requestDto /*,Customer customer*/) {
        /*if (!passwordEncoder.matches(requestDto.getPassword(), customer.getPassword())) {
            throw new PasswordMismatchException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), customer.getPassword())) {
            throw new PasswordMismatchException("새로운 비밀번호와 기존 비밀번호가 동일합니다.");
        }*/
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // Customer 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        // 비밀번호 업데이트
        customer.updatePassword(newPassword);

        // 변경된 비밀번호 저장
        customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long customerId){
        // 고객 엔티티 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        // 이미 탈퇴한 사용자인지 확인합니다.
        if (customer.getDeletedAt() != null) {
            throw new CustomerAlreadyDeletedException(CustomerErrorCode.CUSTOMER_ALREADY_DELETED);
        }

        // 소프트 삭제를 위해 deletedAt 필드를 현재 시간으로 설정
        customer.setDeletedAt(LocalDateTime.now());

        customerRepository.save(customer);
    }

}
