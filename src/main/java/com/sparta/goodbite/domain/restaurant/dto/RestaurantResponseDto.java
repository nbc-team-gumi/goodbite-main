package com.sparta.goodbite.domain.restaurant.dto;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.enums.Category;

public record RestaurantResponseDto(Long restaurantId, String name, String imageUrl, String address,
                                    String area, String phoneNumber, String category,
                                    int capacity) {

    public static RestaurantResponseDto from(Restaurant restaurant) {
        return new RestaurantResponseDto(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getImageUrl(),
            restaurant.getAddress(),
            restaurant.getArea(),
            restaurant.getPhoneNumber(),
            restaurant.getCategory(),
            restaurant.getCapacity());
    }
}