package com.sparta.goodbite.domain.restaurant.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.menu.dto.MenuResponseDto;
import com.sparta.goodbite.domain.menu.service.MenuService;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantIdResponseDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.service.RestaurantService;
import com.sparta.goodbite.domain.waiting.dto.WaitingResponseDto;
import com.sparta.goodbite.domain.waiting.service.WaitingService;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OperatingHourService operatingHourService;
    private final WaitingService waitingService;
    private final MenuService menuService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createRestaurant(
        @Valid @RequestBody RestaurantRequestDto restaurantRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        restaurantService.createRestaurant(restaurantRequestDto, userDetails.getUser());
        return ResponseUtil.createOk();
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<DataResponseDto<RestaurantResponseDto>> getRestaurant(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(restaurantService.getRestaurant(restaurantId));
    }

    @GetMapping("/my")
    public ResponseEntity<DataResponseDto<RestaurantIdResponseDto>> getMyRestaurant(
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {

        return ResponseUtil.findOk(restaurantService.getMyRestaurant(userDetails.getUser()));
    }

    @GetMapping
    public ResponseEntity<DataResponseDto<List<RestaurantResponseDto>>> getAllRestaurants() {
        return ResponseUtil.findOk(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{restaurantId}/operating-hours")
    public ResponseEntity<DataResponseDto<List<OperatingHourResponseDto>>> getAllOperatingHoursByRestaurantId(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(
            operatingHourService.getAllOperatingHoursByRestaurantId(restaurantId));
    }

    @GetMapping("/{restaurantId}/menus")
    public ResponseEntity<DataResponseDto<List<MenuResponseDto>>> getAllMenusByRestaurantId(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(menuService.getAllMenusByRestaurantId(restaurantId));
    }

    // 웨이팅 전체 조회용 api
    // 해당 가게 오너 + Admin
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @GetMapping("/{restaurantId}/waitings")
    public ResponseEntity<DataResponseDto<Page<WaitingResponseDto>>> getAllWaitingsByRestaurantId(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PathVariable Long restaurantId,
        @PageableDefault(size = 5) Pageable pageable) {

        return ResponseUtil.createOk(
            waitingService.getWaitingsByRestaurantId(userDetails.getUser(), restaurantId,
                pageable));
    }

    // 단순 가게의 현재 웨이팅 갯수 받아오는 api
    // api 권한 제한 없음
    @GetMapping("/{restaurantId}/last-waiting")
    public ResponseEntity<DataResponseDto<Long>> getWaitingLastNumber(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(waitingService.findLastOrderNumber(restaurantId));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<MessageResponseDto> updateRestaurant(@PathVariable Long restaurantId,
        @RequestBody RestaurantRequestDto restaurantRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        restaurantService.updateRestaurant(restaurantId, restaurantRequestDto,
            userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    // 가게 주인용 가게 전체 하나씩 웨이팅 줄이기 메서드 호출
    // 해당 가게 오너만 가능
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