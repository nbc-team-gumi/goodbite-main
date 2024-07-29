package com.sparta.goodbite.domain.restaurant.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.menu.dto.MenuResponseDto;
import com.sparta.goodbite.domain.menu.service.MenuService;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import com.sparta.goodbite.domain.operatinghour.service.OperatingHourService;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.service.RestaurantService;
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

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OperatingHourService operatingHourService;
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

    @GetMapping
    public ResponseEntity<DataResponseDto<List<RestaurantResponseDto>>> getAllRestaurants() {
        return ResponseUtil.findOk(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{restaurantId}/operating-hours")
    public ResponseEntity<DataResponseDto<List<OperatingHourResponseDto>>> getAllOperatingHoursByRestaurantId(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(
            restaurantService.getAllOperatingHoursByRestaurantId(restaurantId));
    }

    @GetMapping("/{restaurantId}/menus")
    public ResponseEntity<DataResponseDto<List<MenuResponseDto>>> getAllMenusByRestaurantId(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(menuService.getAllMenusByRestaurantId(restaurantId));
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

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<MessageResponseDto> deleteRestaurant(@PathVariable Long restaurantId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        restaurantService.deleteRestaurant(restaurantId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}