package com.sparta.goodbite.domain.owner.service;

import com.sparta.goodbite.common.UserCredentials;
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
import com.sparta.goodbite.exception.user.UserErrorCode;
import com.sparta.goodbite.exception.user.detail.PasswordMismatchException;
import com.sparta.goodbite.exception.user.detail.SamePasswordException;
import com.sparta.goodbite.exception.user.detail.UserMismatchException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    //가입
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


    //조회
    @Transactional(readOnly = true)
    public OwnerResponseDto getOwner(Long ownerId, String role, UserCredentials user) {
        //본인인지 확인, Owner인지 확인
        /*if (!Objects.equals(user.getId(), ownerId) || !Objects.equals(role,
            "ROLE_OWNER")) {//롤 확인 -> 지금은 확인하는게 나을듯
            throw new UserMismatchException(UserErrorCode.USER_MISMATCH);
        }*/
        if (!Objects.equals(user.getId(), ownerId)) {//롤 확인 -> 지금은 확인하는게 나을듯
            throw new UserMismatchException(UserErrorCode.USER_MISMATCH);
        }

        return OwnerResponseDto.from(ownerRepository.findByEmail(user.getEmail()).orElseThrow(()
            -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND)));
    }

    // 수정-닉네임
    @Transactional
    public void updateNickname(Long ownerId, UpdateOwnerNicknameRequestDto requestDto,
        UserCredentials user) {

        String newNickname = requestDto.getNewNickname();

        // 본인인지 확인 & 권한확인(사장이 맞는지 Owner인지 확인)
        if (!Objects.equals(user.getId(), ownerId)) {
            throw new UserMismatchException(UserErrorCode.USER_MISMATCH);
        }

        //UserCredential타입의 객체를 Owner타입으로 캐스팅
        Owner owner = (Owner) user;

        // Owner 조회 -> 어처피 인증된 객체가 들어온다는건 DB에 있다는 말이기도 하니까 확인할 필요없는거 아닌가?
        /*Owner owner = ownerRepository.findById(user.getId())
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));*/

        // 닉네임 중복 검사
        ownerRepository.findByNickname(newNickname).ifPresent(unused -> {
            throw new DuplicateNicknameException(OwnerErrorCode.DUPLICATE_NICKNAME);
        });

        owner.updateNickname(newNickname);
        ownerRepository.save(owner);
    }

    //수정-전화번호
    @Transactional
    public void updatePhoneNumber(Long ownerId, String role,
        UpdateOwnerPhoneNumberRequestDto requestDto, UserCredentials user) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();

        // 본인인지 확인 & 권한확인(사장이 맞는지.Owner인지 확인)
        if (!Objects.equals(user.getId(), ownerId) || !Objects.equals(role,
            "ROLE_OWNER")) {//롤 확인 -> 지금은 확인하는게 나을듯
            throw new UserMismatchException(UserErrorCode.USER_MISMATCH);
        }

        //UserCredential타입의 객체를 Owner타입으로 캐스팅
        Owner owner = (Owner) user;

        // Owner 조회 -> 어처피 인증된 객체가 들어온다는건 DB에 있다는 말이기도 하니까 확인할 필요없는거 아닌가?
        /*Owner owner = ownerRepository.findById(user.getId())
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));
*/
        // 전화번호 중복 검사
        ownerRepository.findByPhoneNumber(newPhoneNumber).ifPresent(unused -> {
            throw new DuplicatePhoneNumberException(OwnerErrorCode.DUPLICATE_PHONE_NUMBER);
        });

        // 전화번호 업데이트
        owner.updatePhoneNumber(newPhoneNumber);

        // owner 객체가 영속성 컨텍스트에 포함되어 있는지 확인
        ownerRepository.save(owner);  // 이 라인은 엔티티가 이미 영속성 컨텍스트에 있는 경우 생략 가능
    }


    // 수정-사업자번호
    @Transactional
    public void updateBusinessNumber(Long ownerId, UpdateBusinessNumberRequestDto requestDto,
        UserCredentials user) {
        String newBusinessNumber = requestDto.getNewBusinessNumber();

        // Owner 조회
        /*Owner owner = ownerRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));
*/
        // 사업자번호 중복 검사
        ownerRepository.findByBusinessNumber(newBusinessNumber).ifPresent(unused -> {
            throw new DuplicateBusinessNumberException(OwnerErrorCode.DUPLICATE_BUSINESS_NUMBER);
        });

        // 사업자번호 업데이트
        //owner.updateBusinessNumber(newBusinessNumber);

        //사업자번호 인증상태 미인증으로 변환
    }

    @Transactional
    public void updateBusinessNumber(String ownerEmail, UpdateBusinessNumberRequestDto requestDto) {
        String newBusinessNumber = requestDto.getNewBusinessNumber();

        // Owner 조회
        Owner owner = ownerRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        // 사업자번호 중복 검사
        ownerRepository.findByBusinessNumber(newBusinessNumber).ifPresent(unused -> {
            throw new DuplicateBusinessNumberException(OwnerErrorCode.DUPLICATE_BUSINESS_NUMBER);
        });

        // 사업자번호 업데이트
        owner.updateBusinessNumber(newBusinessNumber);

        //사업자번호 인증상태 미인증으로 변환
    }

    // 수정-비밀번호
    @Transactional
    public void updatePassword(String ownerEmail,
        UpdateOwnerPasswordRequestDto requestDto) {
        // Owner 조회
        Owner owner = ownerRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        //
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), owner.getPassword())) {
            throw new PasswordMismatchException(UserErrorCode.PASSWORD_MISMATCH);
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), owner.getPassword())) {
            throw new SamePasswordException(UserErrorCode.SAME_PASSWORD);
        }

        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // 비밀번호 업데이트
        owner.updatePassword(newPassword);

    }

    //삭제
    @Transactional
    public void deleteOwner(String ownerEmail) {
        // Owner 조회
        Owner owner = ownerRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));

        // 이미 탈퇴한 사용자인지 확인합니다.
        if (owner.getDeletedAt() != null) {
            throw new OwnerAlreadyDeletedException(OwnerErrorCode.OWNER_ALREADY_DELETED);
        }

        // 소프트 삭제를 위해 deletedAt 필드를 현재 시간으로 설정
        owner.deactivate();

    }

    /*private void validate(Menu menu, Restaurant restaurant) {
        if (!menu.getRestaurant().getId().equals(restaurant.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }*/

    //정보를 조회하고자 하는 사용자id와

}