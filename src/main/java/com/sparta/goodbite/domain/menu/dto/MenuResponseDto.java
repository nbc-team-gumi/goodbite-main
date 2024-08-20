package com.sparta.goodbite.domain.menu.dto;

import com.sparta.goodbite.domain.menu.entity.Menu;

public record MenuResponseDto(Long menuId, Long restaurantId, int price, String name,
                              String description, String imageUrl) {

    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(
            menu.getId(),
            menu.getRestaurant().getId(),
            menu.getPrice(),
            menu.getName(),
            menu.getDescription(),
            menu.getImageUrl());
    }
}