package com.sparta.goodbite.domain.review.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.review.dto.CreateWaitingReviewRequestDto;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goodbite.domain.review.service.WaitingReviewServiceImpl;
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
@RequestMapping("/waiting-reviews")
@RestController
public class WaitingReviewController {

    private final WaitingReviewServiceImpl waitingReviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createWaitingReview(
        @Valid @RequestBody CreateWaitingReviewRequestDto createWaitingReviewRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        waitingReviewService.createReview(createWaitingReviewRequestDto, userDetails.getUser());
        return ResponseUtil.createOk();
    }

    @GetMapping("/{waitingReviewId}")
    public ResponseEntity<DataResponseDto<ReviewResponseDto>> getWaitingReview(
        @PathVariable Long waitingReviewId) {
        return ResponseUtil.findOk(waitingReviewService.getReview(waitingReviewId));
    }

    @GetMapping
    public ResponseEntity<DataResponseDto<List<ReviewResponseDto>>> getAllWaitingReviews() {
        return ResponseUtil.findOk(waitingReviewService.getAllReviews());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<DataResponseDto<List<ReviewResponseDto>>> getMyWaitingReviews(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        return ResponseUtil.findOk(waitingReviewService.getMyReviews(userDetails.getUser()));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{waitingReviewId}")
    public ResponseEntity<MessageResponseDto> updateWaitingReview(
        @PathVariable Long waitingReviewId,
        @Valid @RequestBody UpdateReviewRequestDto updateReviewRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        waitingReviewService.updateReview(waitingReviewId, updateReviewRequestDto,
            userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{waitingReviewId}")
    public ResponseEntity<MessageResponseDto> deleteWaitingReview(
        @PathVariable Long waitingReviewId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        waitingReviewService.deleteReview(waitingReviewId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}