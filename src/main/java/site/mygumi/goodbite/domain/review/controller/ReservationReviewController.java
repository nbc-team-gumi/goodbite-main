package site.mygumi.goodbite.domain.review.controller;

import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.review.dto.CreateReservationReviewRequestDto;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.dto.UpdateReviewRequestDto;
import site.mygumi.goodbite.domain.review.service.ReservationReviewServiceImpl;
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
@RequestMapping("/reservation-reviews")
@RestController
public class ReservationReviewController {

    private final ReservationReviewServiceImpl reservationReviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createReview(
        @Valid @RequestBody CreateReservationReviewRequestDto createReservationReviewRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reservationReviewService.createReview(createReservationReviewRequestDto,
            userDetails.getUser());
        return ResponseUtil.createOk();
    }

    @GetMapping("/{reservationReviewId}")
    public ResponseEntity<DataResponseDto<ReviewResponseDto>> getReview(
        @PathVariable Long reservationReviewId) {

        return ResponseUtil.findOk(reservationReviewService.getReview(reservationReviewId));
    }

    @GetMapping
    public ResponseEntity<DataResponseDto<List<ReviewResponseDto>>> getAllReviews() {
        return ResponseUtil.findOk(reservationReviewService.getAllReviews());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{reservationReviewId}")
    public ResponseEntity<MessageResponseDto> updateReview(@PathVariable Long reservationReviewId,
        @Valid @RequestBody UpdateReviewRequestDto updateReviewRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reservationReviewService.updateReview(reservationReviewId, updateReviewRequestDto,
            userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @DeleteMapping("/{reservationReviewId}")
    public ResponseEntity<MessageResponseDto> deleteReview(@PathVariable Long reservationReviewId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reservationReviewService.deleteReview(reservationReviewId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}
