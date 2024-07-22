package com.sparta.goodbite.domain.review.service;

import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.menu.repository.MenuRepository;
import com.sparta.goodbite.domain.review.dto.CreateReviewRequestDto;
import com.sparta.goodbite.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    
    @Transactional
    public void createReview(CreateReviewRequestDto createReviewRequestDto) {
        Menu menu = menuRepository.findByIdOrThrow(createReviewRequestDto.getMenuId());
        reviewRepository.save(createReviewRequestDto.toEntity(menu));
    }
}