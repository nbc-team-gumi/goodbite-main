package com.sparta.goodbite.domain.restaurant.dto;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;

public record RestaurantResponseDto(Long ownerId, String name, String picture, String address,
                                    String area, String telno, String category) {

    public RestaurantResponseDto(Restaurant restaurant) {
        this(restaurant.getOwnerId(), restaurant.getName(), restaurant.getPicture(),
            restaurant.getAddress(), restaurant.getArea(), restaurant.getTelno(),
            restaurant.getCategory());
    }

}
