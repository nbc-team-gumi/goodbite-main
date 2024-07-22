package com.sparta.goodbite.domain.restaurant.controller;

import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<MessageResponseDto> createRestaurant(
        @Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {

        restaurantService.createRestaurant(restaurantRequestDto);
        return ResponseUtil.createOk();
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<DataResponseDto<RestaurantResponseDto>> getRestaurant(
        @PathVariable Long restaurantId) {

        RestaurantResponseDto restaurantResponseDto = restaurantService.getRestaurant(restaurantId);
        return ResponseUtil.findOk(restaurantResponseDto);
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<MessageResponseDto> updateRestaurant(@PathVariable Long restaurantId,
        @RequestBody RestaurantRequestDto restaurantRequestDto) {

        restaurantService.updateRestaurant(restaurantId, restaurantRequestDto);
        return ResponseUtil.updateOk();
    }

}
