package site.mygumi.goodbite.domain.reservation.controller;

import site.mygumi.goodbite.security.EmailUserDetails;
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.reservation.dto.CreateReservationRequestDto;
import site.mygumi.goodbite.domain.reservation.dto.ReservationResponseDto;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<DataResponseDto<Page<ReservationResponseDto>>> getMyReservations(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PageableDefault(size = Reservation.DEFAULT_PAGE_SIZE) Pageable pageable) {

        return ResponseUtil.findOk(
            reservationService.getMyReservations(userDetails.getUser(), pageable));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<MessageResponseDto> deleteReservation(@PathVariable Long reservationId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        reservationService.deleteReservation(reservationId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}