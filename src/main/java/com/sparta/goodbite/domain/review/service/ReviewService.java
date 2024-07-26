package com.sparta.goodbite.domain.review.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.menu.repository.MenuRepository;
import com.sparta.goodbite.domain.review.dto.CreateReviewRequestDto;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goodbite.domain.review.entity.Review;
import com.sparta.goodbite.domain.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void createReview(CreateReviewRequestDto createReviewRequestDto, UserCredentials user) {
        Menu menu = menuRepository.findByIdOrThrow(createReviewRequestDto.getMenuId());
        Customer customer = null;
        reviewRepository.save(createReviewRequestDto.toEntity(menu, customer));
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(reviewRepository.findByIdOrThrow(reviewId));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        Review review = reviewRepository.findByIdOrThrow(reviewId);
        review.update(updateReviewRequestDto);
    }

    @Transactional
    public void deleteReview(Long reviewId, UserCredentials user) {
        Review review = reviewRepository.findByIdOrThrow(reviewId);
        reviewRepository.delete(review);
    }
}