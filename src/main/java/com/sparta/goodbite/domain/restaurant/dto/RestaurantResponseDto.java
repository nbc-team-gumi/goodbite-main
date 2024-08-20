package com.sparta.goodbite.domain.restaurant.dto;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.enums.Category;

public record RestaurantResponseDto(Long restaurantId, String name, String imageUrl, String sido,
                                    String sigungu, String address, String detailAddress,
                                    String phoneNumber, Category category, int capacity,
                                    double rating) {

    public static RestaurantResponseDto from(Restaurant restaurant) {
        return new RestaurantResponseDto(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getImageUrl(),
            restaurant.getSido(),
            restaurant.getSigungu(),
            restaurant.getAddress(),
            restaurant.getDetailAddress(),
            restaurant.getPhoneNumber(),
            restaurant.getCategory(),
            restaurant.getCapacity(),
            Math.round(restaurant.getRating() * 10) / 10.0);
    }
}