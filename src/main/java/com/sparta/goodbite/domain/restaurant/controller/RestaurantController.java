package com.sparta.goodbite.domain.restaurant.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.menu.dto.MenuResponseDto;
import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.menu.service.MenuService;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import com.sparta.goodbite.domain.operatinghour.service.OperatingHourService;
import com.sparta.goodbite.domain.reservation.dto.ReservationResponseDto;
import com.sparta.goodbite.domain.reservation.entity.Reservation;
import com.sparta.goodbite.domain.reservation.service.ReservationService;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantIdResponseDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.service.RestaurantService;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.entity.Review;
import com.sparta.goodbite.domain.review.service.ReservationReviewServiceImpl;
import com.sparta.goodbite.domain.review.service.ReviewService;
import com.sparta.goodbite.domain.review.service.WaitingReviewServiceImpl;
import com.sparta.goodbite.domain.waiting.dto.WaitingResponseDto;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.service.WaitingService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OperatingHourService operatingHourService;
    private final WaitingService waitingService;
    private final MenuService menuService;
    private final WaitingReviewServiceImpl waitingReviewService;
    private final ReservationReviewServiceImpl reservationReviewService;
    private final ReservationService reservationService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createRestaurant(
        @Valid @RequestPart RestaurantRequestDto restaurantRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @RequestPart MultipartFile image) {

        restaurantService.createRestaurant(restaurantRequestDto, userDetails.getUser(), image);
        return ResponseUtil.createOk();
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<DataResponseDto<RestaurantResponseDto>> getRestaurant(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(restaurantService.getRestaurant(restaurantId));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/my")
    public ResponseEntity<DataResponseDto<RestaurantIdResponseDto>> getMyRestaurant(
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        return ResponseUtil.findOk(restaurantService.getMyRestaurant(userDetails.getUser()));
    }

    @GetMapping
    public ResponseEntity<DataResponseDto<Page<RestaurantResponseDto>>> getAllRestaurants(
        @PageableDefault(size = Restaurant.DEFAULT_PAGE_SIZE, sort = "name") Pageable pageable) {

        return ResponseUtil.findOk(restaurantService.getAllRestaurants(pageable));
    }

    @GetMapping("/{restaurantId}/operating-hours")
    public ResponseEntity<DataResponseDto<List<OperatingHourResponseDto>>> getAllOperatingHoursByRestaurantId(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(
            operatingHourService.getAllOperatingHoursByRestaurantId(restaurantId));
    }

    @GetMapping("/{restaurantId}/menus")
    public ResponseEntity<DataResponseDto<Page<MenuResponseDto>>> getAllMenusByRestaurantId(
        @PathVariable Long restaurantId,
        @PageableDefault(size = Menu.DEFAULT_PAGE_SIZE) Pageable pageable) {

        return ResponseUtil.findOk(menuService.getAllMenusByRestaurantId(restaurantId, pageable));
    }

    // 사업자 대시보드용 전체 웨이팅 조회
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @GetMapping("/{restaurantId}/waitings")
    public ResponseEntity<DataResponseDto<Page<WaitingResponseDto>>> getAllWaitingsByRestaurantId(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long restaurantId,
        @PageableDefault(size = Waiting.DEFAULT_PAGE_SIZE) Pageable pageable) {

        return ResponseUtil.createOk(
            waitingService.getWaitingsByRestaurantId(userDetails.getUser(), restaurantId,
                pageable));
    }

    // 가게의 마지막 웨이팅 번호 조회
    @GetMapping("/{restaurantId}/last-waiting")
    public ResponseEntity<DataResponseDto<Long>> getWaitingLastNumber(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(waitingService.findLastOrderNumber(restaurantId));
    }

    @GetMapping("/{restaurantId}/reviews")
    public ResponseEntity<DataResponseDto<Page<ReviewResponseDto>>> getAllReviewsByRestaurantId(
        @PathVariable Long restaurantId,
        @PageableDefault(size = Review.DEFAULT_PAGE_SIZE) Pageable pageable) {

        List<ReviewResponseDto> reservationReviews = reservationReviewService.getAllReviewsByRestaurantId(
            restaurantId);
        List<ReviewResponseDto> waitingReviews = waitingReviewService.getAllReviewsByRestaurantId(
            restaurantId);

        return ResponseUtil.findOk(
            ReviewService.getAllReviewsSortedAndPaged(pageable, reservationReviews,
                waitingReviews));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{restaurantId}/reservations")
    public ResponseEntity<DataResponseDto<Page<ReservationResponseDto>>> getAllReservationsByRestaurantId(
        @PathVariable Long restaurantId, @AuthenticationPrincipal EmailUserDetails userDetails,
        @PageableDefault(size = Reservation.DEFAULT_PAGE_SIZE) Pageable pageable) {

        return ResponseUtil.findOk(reservationService.getAllReservationsByRestaurantId(restaurantId,
            userDetails.getUser(), pageable));
    }

    @GetMapping("/{restaurantId}/capacity")
    public ResponseEntity<DataResponseDto<Integer>> getAvailableCapacity(
        @PathVariable Long restaurantId,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        return ResponseUtil.findOk(
            reservationService.getAvailableCapacity(restaurantId, date, time));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<MessageResponseDto> updateRestaurant(@PathVariable Long restaurantId,
        @RequestPart RestaurantRequestDto restaurantRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @RequestPart(required = false) MultipartFile image) {

        restaurantService.updateRestaurant(restaurantId, restaurantRequestDto,
            userDetails.getUser(), image);
        return ResponseUtil.updateOk();
    }

    // 가게 주인용 가게 전체 하나씩 웨이팅 줄이기 메서드 호출
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @PutMapping("/{restaurantId}/waitings")
    public ResponseEntity<MessageResponseDto> reduceAllWaitingOrders(
        @PathVariable Long restaurantId, @AuthenticationPrincipal EmailUserDetails userDetails) {

        waitingService.reduceAllWaitingOrders(userDetails.getUser(), restaurantId);
        return ResponseUtil.updateOk();
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<MessageResponseDto> deleteRestaurant(@PathVariable Long restaurantId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        restaurantService.deleteRestaurant(restaurantId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}