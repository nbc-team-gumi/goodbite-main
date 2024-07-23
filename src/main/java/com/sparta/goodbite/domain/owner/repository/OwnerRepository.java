package com.sparta.goodbite.domain.owner.repository;

import com.sparta.goodbite.domain.owner.entity.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByNickname(String nickname);

    Optional<Owner> findByEmail(String email);

    Optional<Owner> findByPhoneNumber(String phoneNumber);

    Optional<Owner> findByBusinessNumber(String businessNumber);
}
