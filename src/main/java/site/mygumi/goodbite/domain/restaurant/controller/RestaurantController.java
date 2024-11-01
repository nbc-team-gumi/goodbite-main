package site.mygumi.goodbite.domain.restaurant.controller;

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
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.menu.dto.MenuResponseDto;
import site.mygumi.goodbite.domain.menu.entity.Menu;
import site.mygumi.goodbite.domain.menu.service.MenuService;
import site.mygumi.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import site.mygumi.goodbite.domain.operatinghour.service.OperatingHourService;
import site.mygumi.goodbite.domain.reservation.dto.ReservationResponseDto;
import site.mygumi.goodbite.domain.reservation.entity.Reservation;
import site.mygumi.goodbite.domain.reservation.service.ReservationService;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantIdResponseDto;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.enums.Category;
import site.mygumi.goodbite.domain.restaurant.service.RestaurantService;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import site.mygumi.goodbite.domain.review.entity.Review;
import site.mygumi.goodbite.domain.review.service.ReservationReviewServiceImpl;
import site.mygumi.goodbite.domain.review.service.TotalReviewService;
import site.mygumi.goodbite.domain.review.service.WaitingReviewServiceImpl;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;
import site.mygumi.goodbite.domain.waiting.dto.WaitingResponseDto;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.service.WaitingService;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OperatingHourService operatingHourService;
    private final WaitingService waitingService;
    private final MenuService menuService;
    private final ReservationService reservationService;
    private final TotalReviewService totalReviewService;
    private final WaitingReviewServiceImpl waitingReviewService;
    private final ReservationReviewServiceImpl reservationReviewService;

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
        @PathVariable Long restaurantId,
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PageableDefault(size = Waiting.DEFAULT_PAGE_SIZE) Pageable pageable) {

        return ResponseUtil.createOk(
            waitingService.getWaitingsByRestaurantId(restaurantId, userDetails.getUser(),
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
            totalReviewService.getAllReviewsSortedAndPaged(pageable, reservationReviews,
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

    @GetMapping("/filter")
    public ResponseEntity<DataResponseDto<Page<RestaurantResponseDto>>> getFilteredRestaurants(
        @RequestParam(required = false) String sido,
        @RequestParam(required = false) String sigungu,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) Double rating,
        @PageableDefault(size = Restaurant.DEFAULT_PAGE_SIZE) Pageable pageable) {

        return ResponseUtil.findOk(
            restaurantService.getFilteredRestaurants(sido, sigungu, category, rating, pageable));
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
    @PutMapping("/{restaurantId}/admittance")
    public ResponseEntity<MessageResponseDto> admitWaitingCustomer(
        @PathVariable Long restaurantId, @AuthenticationPrincipal EmailUserDetails userDetails) {

        waitingService.reduceAllWaitingOrders(restaurantId, userDetails.getUser());
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