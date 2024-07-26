package com.sparta.goodbite.domain.restaurant.dto;

import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;

public record RestaurantResponseDto(Owner owner, String name, String imageUrl, String address,
                                    String area, String phoneNumber, String category) {

    public static RestaurantResponseDto from(Restaurant restaurant) {
        return new RestaurantResponseDto(
            restaurant.getOwner(),
            restaurant.getName(),
            restaurant.getImageUrl(),
            restaurant.getAddress(),
            restaurant.getArea(),
            restaurant.getPhoneNumber(),
            restaurant.getCategory());
    }

}
