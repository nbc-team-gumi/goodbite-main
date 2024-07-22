package com.sparta.goodbite.domain.review.controller;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.review.dto.CreateReviewRequestDto;
import com.sparta.goodbite.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createReview(
        @RequestBody @Valid CreateReviewRequestDto createReviewRequestDto) {

        reviewService.createReview(createReviewRequestDto);
        return ResponseUtil.createOk();
    }
}