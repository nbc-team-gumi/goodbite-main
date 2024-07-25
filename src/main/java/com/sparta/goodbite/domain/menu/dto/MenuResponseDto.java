package com.sparta.goodbite.domain.menu.dto;

import com.sparta.goodbite.domain.menu.entity.Menu;

public record MenuResponseDto(int price, String name, String description) {

    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(
            menu.getPrice(),
            menu.getName(),
            menu.getDescription());
    }
}