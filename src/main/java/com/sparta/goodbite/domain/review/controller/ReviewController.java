package com.sparta.goodbite.domain.review.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.review.dto.CreateReviewRequestDto;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goodbite.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createReview(
        @Valid @RequestBody CreateReviewRequestDto createReviewRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reviewService.createReview(createReviewRequestDto, userDetails.getUser());
        return ResponseUtil.createOk();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<DataResponseDto<ReviewResponseDto>> getReview(
        @PathVariable Long reviewId) {

        return ResponseUtil.findOk(reviewService.getReview(reviewId));
    }

    @GetMapping
    public ResponseEntity<DataResponseDto<List<ReviewResponseDto>>> getAllReviews() {
        return ResponseUtil.findOk(reviewService.getAllReviews());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<MessageResponseDto> updateReview(@PathVariable Long reviewId,
        @Valid @RequestBody UpdateReviewRequestDto updateReviewRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reviewService.updateReview(reviewId, updateReviewRequestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponseDto> deleteReview(@PathVariable Long reviewId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reviewService.deleteReview(reviewId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}