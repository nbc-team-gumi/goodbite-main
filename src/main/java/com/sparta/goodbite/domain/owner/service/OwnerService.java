package com.sparta.goodbite.domain.owner.service;

import com.sparta.goodbite.domain.owner.dto.OwnerResponseDto;
import com.sparta.goodbite.domain.owner.dto.OwnerSignUpRequestDto;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.detail.DuplicateBusinessNumberException;
import com.sparta.goodbite.exception.owner.detail.DuplicateEmailException;
import com.sparta.goodbite.exception.owner.detail.DuplicateNicknameException;
import com.sparta.goodbite.exception.owner.detail.DuplicatePhoneNumberException;
import com.sparta.goodbite.exception.owner.detail.OwnerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(OwnerSignUpRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        String email = requestDto.getEmail();
        String phoneNumber = requestDto.getPhoneNumber();
        String businessNumber = requestDto.getBusinessNumber();

        // 닉네임 중복 검사
        ownerRepository.findByNickname(nickname).ifPresent(u -> {
            throw new DuplicateNicknameException(OwnerErrorCode.DUPLICATE_NICKNAME);
        });
        // 이메일 중복 검사
        ownerRepository.findByEmail(email).ifPresent(u -> {
            throw new DuplicateEmailException(OwnerErrorCode.DUPLICATE_EMAIL);
        });
        // 전화번호 중복 검사
        ownerRepository.findByPhoneNumber(phoneNumber).ifPresent(u -> {
            throw new DuplicatePhoneNumberException(OwnerErrorCode.DUPLICATE_PHONE_NUMBER);
        });
        // 사업자번호 중복 검사
        ownerRepository.findByBusinessNumber(businessNumber).ifPresent(u -> {
            throw new DuplicateBusinessNumberException(OwnerErrorCode.DUPLICATE_BUSINESS_NUMBER);
        });

        // 비밀번호 암호화 -> 인증인가연결시 config에서 PasswordEncoder Bean등록
        String password = passwordEncoder.encode(requestDto.getPassword());

        // User DB에 저장
        Owner owner = Owner.builder()
            .email(email)
            .nickname(nickname)
            .password(password)
            .phoneNumber(phoneNumber)
            .businessNumber(businessNumber)
            .build();

        ownerRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public OwnerResponseDto getOwner(Long ownerId) {
        return OwnerResponseDto.from(ownerRepository.findById(ownerId).orElseThrow(()
            -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND)));
    }


}
