package site.mygumi.goodbite.domain.user.owner.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.detail.DuplicateBusinessNumberException;
import site.mygumi.goodbite.domain.user.owner.exception.detail.DuplicateEmailException;
import site.mygumi.goodbite.domain.user.owner.exception.detail.DuplicateNicknameException;
import site.mygumi.goodbite.domain.user.owner.exception.detail.DuplicatePhoneNumberException;
import site.mygumi.goodbite.domain.user.owner.exception.detail.OwnerNotFoundException;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByNickname(String nickname);

    Optional<Owner> findByEmail(String email);

    Optional<Owner> findByEmailAndDeletedAtIsNull(String email);

    Optional<Owner> findByPhoneNumber(String phoneNumber);

    Optional<Owner> findByBusinessNumber(String businessNumber);

    default Owner findByIdOrThrow(Long ownerId) {
        return findById(ownerId).orElseThrow(() -> new OwnerNotFoundException(
            OwnerErrorCode.OWNER_NOT_FOUND
        ));
    }

    default Owner findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new OwnerNotFoundException(
            OwnerErrorCode.OWNER_NOT_FOUND
        ));
    }

    //닉네임 중복 확인 메서드
    default void validateDuplicateNickname(String nickname) {
        findByNickname(nickname).ifPresent(_owner -> {
            throw new DuplicateNicknameException(OwnerErrorCode.DUPLICATE_NICKNAME);
        });
    }

    //이메일 중복 확인 메서드
    default void validateDuplicateEmail(String email) {
        findByEmail(email).ifPresent(_owner -> {
            throw new DuplicateEmailException(OwnerErrorCode.DUPLICATE_EMAIL);
        });
    }

    //사업자번호 중복 확인 메서드
    default void validateDuplicateBusinessNumber(String businessNumber) {
        findByBusinessNumber(businessNumber).ifPresent(_owner -> {
            throw new DuplicateBusinessNumberException(OwnerErrorCode.DUPLICATE_BUSINESS_NUMBER);
        });
    }

    //전화번호 중복 확인 메서드
    default void validateDuplicatePhoneNumber(String phoneNumber) {
        findByPhoneNumber(phoneNumber).ifPresent(_owner -> {
            throw new DuplicatePhoneNumberException(OwnerErrorCode.DUPLICATE_PHONE_NUMBER);
        });
    }
}