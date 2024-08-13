package com.sparta.goodbite.domain.reservation.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.reservation.dto.CreateReservationRequestDto;
import com.sparta.goodbite.domain.reservation.dto.ReservationResponseDto;
import com.sparta.goodbite.domain.reservation.service.ReservationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createReservation(
        @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reservationService.createReservation(createReservationRequestDto, userDetails.getUser());
        return ResponseUtil.createOk();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
    @GetMapping("/{reservationId}")
    public ResponseEntity<DataResponseDto<ReservationResponseDto>> getReservation(
        @PathVariable Long reservationId, @AuthenticationPrincipal EmailUserDetails userDetails) {

        return ResponseUtil.findOk(
            reservationService.getReservation(reservationId, userDetails.getUser()));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<DataResponseDto<List<ReservationResponseDto>>> getMyReservations(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        return ResponseUtil.findOk(reservationService.getMyReservations(userDetails.getUser()));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<MessageResponseDto> deleteReservation(@PathVariable Long reservationId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reservationService.deleteReservation(reservationId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}