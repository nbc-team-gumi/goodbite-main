package com.sparta.goodbite.domain.waiting.repository;

import com.sparta.goodbite.domain.waiting.entity.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

}
