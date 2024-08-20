package com.sparta.goodbite.domain.review.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.entity.Review;
import com.sparta.goodbite.domain.review.service.ReservationReviewServiceImpl;
import com.sparta.goodbite.domain.review.service.TotalReviewService;
import com.sparta.goodbite.domain.review.service.WaitingReviewServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private final WaitingReviewServiceImpl waitingReviewService;
    private final ReservationReviewServiceImpl reservationReviewService;
    private final TotalReviewService totalReviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<DataResponseDto<Page<ReviewResponseDto>>> getMyReviews(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PageableDefault(size = Review.DEFAULT_PAGE_SIZE) Pageable pageable) {

        List<ReviewResponseDto> reservationReviews = reservationReviewService.getMyReviews(
            userDetails.getUser());
        List<ReviewResponseDto> waitingReviews = waitingReviewService.getMyReviews(
            userDetails.getUser());

        return ResponseUtil.findOk(
            totalReviewService.getAllReviewsSortedAndPaged(pageable, reservationReviews,
                waitingReviews));
    }
}