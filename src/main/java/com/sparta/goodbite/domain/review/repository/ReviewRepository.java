package com.sparta.goodbite.domain.review.repository;

import com.sparta.goodbite.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}