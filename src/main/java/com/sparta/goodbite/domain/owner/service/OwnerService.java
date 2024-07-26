package com.sparta.goodbite.domain.owner.service;

import com.sparta.goodbite.domain.owner.dto.OwnerResponseDto;
import com.sparta.goodbite.domain.owner.dto.OwnerSignUpRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateBusinessNumberRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateOwnerNicknameRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateOwnerPasswordRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateOwnerPhoneNumberRequestDto;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.detail.DuplicateBusinessNumberException;
import com.sparta.goodbite.exception.owner.detail.DuplicateEmailException;
import com.sparta.goodbite.exception.owner.detail.DuplicateNicknameException;
import com.sparta.goodbite.exception.owner.detail.DuplicatePhoneNumberException;
import com.sparta.goodbite.exception.owner.detail.OwnerAlreadyDeletedException;
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
    public void signup(OwnerSignUpRequestDto requestDto) {
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


    @Transactional
    public void updateBusinessNumber(Long ownerId, UpdateBusinessNumberRequestDto requestDto) {
        String newBusinessNumber = requestDto.getNewBusinessNumber();

        // Owner 조회
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        // 사업자번호 중복 검사
        ownerRepository.findByBusinessNumber(newBusinessNumber).ifPresent(unused -> {
            throw new DuplicateBusinessNumberException(OwnerErrorCode.DUPLICATE_BUSINESS_NUMBER);
        });

        // 사업자번호 업데이트
        owner.updateBusinessNumber(newBusinessNumber);

        //사업자번호 인증상태 미인증으로 변환
    }

    @Transactional
    public void updatePassword(Long ownerId,
        UpdateOwnerPasswordRequestDto requestDto /*,Owner owner*/) {
        /*if (!passwordEncoder.matches(requestDto.getPassword(), owner.getPassword())) {
            throw new PasswordMismatchException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), owner.getPassword())) {
            throw new PasswordMismatchException("새로운 비밀번호와 기존 비밀번호가 동일합니다.");
        }*/
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // Owner 조회
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        // 비밀번호 업데이트
        owner.updatePassword(newPassword);

    }

    @Transactional
    public void updatePhoneNumber(Long ownerId, UpdateOwnerPhoneNumberRequestDto requestDto) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();

        // Owner 조회
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        // 전화번호 중복 검사
        ownerRepository.findByPhoneNumber(newPhoneNumber).ifPresent(unused -> {
            throw new DuplicatePhoneNumberException(OwnerErrorCode.DUPLICATE_PHONE_NUMBER);
        });

        // 전화번호 업데이트
        owner.updatePhoneNumber(newPhoneNumber);
    }

    @Transactional
    public void updateNickname(Long ownerId, UpdateOwnerNicknameRequestDto requestDto) {
        String newNickname = requestDto.getNewNickname();

        // Owner 조회
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        // 닉네임 중복 검사
        ownerRepository.findByNickname(newNickname).ifPresent(unused -> {
            throw new DuplicateNicknameException(OwnerErrorCode.DUPLICATE_NICKNAME);
        });

        owner.updateNickname(newNickname);
    }

    @Transactional
    public void deleteOwner(Long ownerId) {
        // Owner 조회
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        // 이미 탈퇴한 사용자인지 확인합니다.
        if (owner.getDeletedAt() != null) {
            throw new OwnerAlreadyDeletedException(OwnerErrorCode.OWNER_ALREADY_DELETED);
        }

        // 소프트 삭제를 위해 deletedAt 필드를 현재 시간으로 설정
        owner.deactivate();

    }
}
