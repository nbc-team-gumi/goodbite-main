package com.sparta.goodbite.domain.owner.repository;

import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.detail.OwnerNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByNickname(String nickname);

    Optional<Owner> findByEmail(String email);

    Optional<Owner> findByPhoneNumber(String phoneNumber);

    Optional<Owner> findByBusinessNumber(String businessNumber);

    default Owner findByIdOrThrow(Long ownerId) {
        return findById(ownerId).orElseThrow(
            () -> new OwnerNotFoundException(OwnerErrorCode.OWNER_NOT_FOUND));
    }
}