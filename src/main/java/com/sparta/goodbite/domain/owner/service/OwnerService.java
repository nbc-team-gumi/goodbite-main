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
import com.sparta.goodbite.exception.owner.detail.InvalidBusinessNumberException;
import com.sparta.goodbite.exception.owner.detail.OwnerAlreadyDeletedException;
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
        validateDuplicateFields(requestDto.getNickname(), requestDto.getEmail(),
            requestDto.getPhoneNumber(), requestDto.getBusinessNumber());

        // 비밀번호 암호화 -> 인증인가연결시 config에서 PasswordEncoder Bean등록
        String password = passwordEncoder.encode(requestDto.getPassword());

        // User DB에 저장
        Owner owner = Owner.builder()
            .email(requestDto.getEmail())
            .nickname(requestDto.getNickname())
            .password(password)
            .phoneNumber(requestDto.getPhoneNumber())
            .businessNumber(requestDto.getBusinessNumber())
            .build();

        ownerRepository.save(owner);
    }


    //조회
    @Transactional(readOnly = true)
    public OwnerResponseDto getOwner(Long ownerId, UserCredentials user) {
        validateOwnerAccess(ownerId, user);//본인인지 확인
        return OwnerResponseDto.from(ownerRepository.findByEmailOrThrow(user.getEmail()));
    }

    // 수정-닉네임
    @Transactional
    public void updateNickname(Long ownerId, UpdateOwnerNicknameRequestDto requestDto,
        UserCredentials user) {
        String newNickname = requestDto.getNewNickname();
        validateOwnerAccess(ownerId, user); //본인확인
        ownerRepository.validateDuplicateNickname(newNickname); //중복닉네임확인

        //UserCredential타입의 객체를 Owner타입으로 캐스팅
        Owner owner = (Owner) user;

        owner.updateNickname(newNickname);
        ownerRepository.save(owner);
    }

    //수정-전화번호
    @Transactional
    public void updatePhoneNumber(Long ownerId, UpdateOwnerPhoneNumberRequestDto requestDto,
        UserCredentials user) {
        String newPhoneNumber = requestDto.getNewPhoneNumber();

        validateOwnerAccess(ownerId, user);
        ownerRepository.validateDuplicatePhoneNumber(newPhoneNumber);

        //UserCredential타입의 객체를 Owner타입으로 캐스팅
        Owner owner = (Owner) user;

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

        // 사업자 등록번호 유효성 검사
        if (!isValidBusinessNumber(newBusinessNumber)) {
            throw new InvalidBusinessNumberException(OwnerErrorCode.INVALID_BUSINESS_NUMBER);
        }

        validateOwnerAccess(ownerId, user);
        ownerRepository.validateDuplicateBusinessNumber(requestDto.getNewBusinessNumber());

        // UserCredential타입의 객체를 Owner타입으로 캐스팅
        Owner owner = (Owner) user;

        // 사업자번호 업데이트
        owner.updateBusinessNumber(newBusinessNumber);

        // 명시적으로 저장
        ownerRepository.save(owner);

        // 비즈니스 로직 추가 필요
        //사업자번호 인증상태 미인증으로 변환
    }

    // 수정-비밀번호
    @Transactional
    public void updatePassword(Long ownerId,
        UpdateOwnerPasswordRequestDto requestDto, UserCredentials user) {
        validateOwnerAccess(ownerId, user);

        //UserCredential타입의 객체를 Owner타입으로 캐스팅
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

        //명시적으로 저장
        ownerRepository.save(owner);

    }

    //삭제
    @Transactional
    public void deleteOwner(Long ownerId, UserCredentials user) {
        validateOwnerAccess(ownerId, user);

        //UserCredential타입의 객체를 Owner타입으로 캐스팅
        Owner owner = (Owner) user;

        // 이미 탈퇴한 사용자인지 확인
        if (owner.getDeletedAt() != null) {
            throw new OwnerAlreadyDeletedException(OwnerErrorCode.OWNER_ALREADY_DELETED);
        }

        // 소프트 삭제를 위해 deletedAt 필드를 현재 시간으로 설정
        owner.deactivate();

        //명시적으로 저장
        ownerRepository.save(owner);
    }

    // 중복 필드 검증 메서드
    private void validateDuplicateFields(String nickname, String email, String phoneNumber,
        String businessNumber) {
        ownerRepository.validateDuplicateNickname(nickname);
        ownerRepository.validateDuplicateEmail(email);
        ownerRepository.validateDuplicatePhoneNumber(phoneNumber);
        ownerRepository.validateDuplicateBusinessNumber(businessNumber);
    }

    //권한이 있는 유저인지 검증
    private void validateOwnerAccess(Long ownerId, UserCredentials user) {
        if (!Objects.equals(user.getId(), ownerId)) {
            throw new UserMismatchException(UserErrorCode.USER_MISMATCH);
        }
    }

    //유효하지 않은 사업자 등록번호를 사전에 필터링
    private boolean isValidBusinessNumber(String businessNumber) {
        //dto에서 유효성검사를 하고 들어오긴하지만 일단은 체크
        if (businessNumber == null || businessNumber.length() != 10) {
            return false;
        }

        //인증키
        int[] checkArr = {1, 3, 7, 1, 3, 7, 1, 3, 1};
        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += (businessNumber.charAt(i) - '0') * checkArr[i];
        }

        sum += ((businessNumber.charAt(8) - '0') * 5) / 10;
        int remainder = sum % 10;
        int checkDigit = (10 - remainder) % 10;

        //계산된 체크 디지트와 사업자 등록번호의 마지막 자리 숫자가 일치하는지 비교
        return checkDigit == (businessNumber.charAt(9) - '0');
    }
}