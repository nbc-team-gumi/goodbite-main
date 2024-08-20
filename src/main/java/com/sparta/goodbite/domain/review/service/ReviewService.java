package com.sparta.goodbite.domain.review.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface ReviewService<T, U> {

    static Page<ReviewResponseDto> getAllReviewsSortedAndPaged(Pageable pageable,
        List<ReviewResponseDto> reservationReviews, List<ReviewResponseDto> waitingReviews) {

        List<ReviewResponseDto> allReviews = new ArrayList<>();
        allReviews.addAll(waitingReviews);
        allReviews.addAll(reservationReviews);

        allReviews.sort(Comparator.comparing(ReviewResponseDto::createdAt).reversed());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allReviews.size());
        return new PageImpl<>(allReviews.subList(start, end), pageable, allReviews.size());
    }

    void createReview(T dto, UserCredentials user);

    ReviewResponseDto getReview(Long reviewId);

    List<ReviewResponseDto> getAllReviews();

    List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId);

    List<ReviewResponseDto> getMyReviews(UserCredentials user);

    void updateReview(Long reviewId, U dto, UserCredentials user);

    void deleteReview(Long reviewId, UserCredentials user);
}