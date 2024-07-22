package com.sparta.goodbite.domain.customer.service;

import com.sparta.goodbite.domain.customer.dto.CustomerSignUpRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateNicknameRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateTelNoRequestDto;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.customer.repository.CustomerRepository;
import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.detail.CustomerNotFoundException;
import com.sparta.goodbite.exception.customer.detail.DuplicateEmailException;
import com.sparta.goodbite.exception.customer.detail.DuplicateNicknameException;
import com.sparta.goodbite.exception.customer.detail.DuplicateTelnoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    //private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(CustomerSignUpRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        String email = requestDto.getEmail();
        String telNo = requestDto.getTelNo();

        // 닉네임 중복 검사
        customerRepository.findByNickname(nickname).ifPresent(u -> {
            throw new DuplicateNicknameException(CustomerErrorCode.DUPLICATE_NICKNAME);
        });
        // 이메일 중복 검사
        customerRepository.findByEmail(email).ifPresent(u -> {
            throw new DuplicateEmailException(CustomerErrorCode.DUPLICATE_EMAIL);
        });
        // 전화번호 중복 검사
        customerRepository.findByTelNo(telNo).ifPresent(u -> {
            throw new DuplicateTelnoException(CustomerErrorCode.DUPLICATE_TELNO);
        });

        // 비밀번호 암호화 -> 인증인가연결시 config에서 PasswordEncoder Bean등록
        //String password = passwordEncoder.encode(requestDto.getPassword());
        //임의로 암호화 패스워드 설정
        String password = requestDto.getPassword();

        // User DB에 저장
        Customer customer = Customer.builder()
            .email(email)
            .nickname(nickname)
            .password(password)
            .telNo(telNo)
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
    public void updateTelNo(Long customerId, UpdateTelNoRequestDto requestDto) {
        String newNewTelNo = requestDto.getNewTelNo();

        // 전화번호 중복 검사
        customerRepository.findByTelNo(newNewTelNo).ifPresent(u -> {
            throw new DuplicateTelnoException(CustomerErrorCode.DUPLICATE_TELNO);
        });

        // Customer 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

        // 전화번호 업데이트
        customer.updateTelNo(newNewTelNo);
    }
}
