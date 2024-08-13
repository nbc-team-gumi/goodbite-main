package com.sparta.goodbite.domain.review.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.service.ReservationReviewServiceImpl;
import com.sparta.goodbite.domain.review.service.WaitingReviewServiceImpl;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<DataResponseDto<List<ReviewResponseDto>>> getMyReviews(
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        List<ReviewResponseDto> waitingReviews = waitingReviewService.getMyReviews(
            userDetails.getUser());
        List<ReviewResponseDto> reservationReviews = reservationReviewService.getMyReviews(
            userDetails.getUser());

        List<ReviewResponseDto> allReviews = new ArrayList<>();
        allReviews.addAll(waitingReviews);
        allReviews.addAll(reservationReviews);

        // createdAt을 기준으로 정렬합니다.
        allReviews.sort(Comparator.comparing(ReviewResponseDto::createdAt).reversed());

        return ResponseUtil.findOk(allReviews);
    }
}