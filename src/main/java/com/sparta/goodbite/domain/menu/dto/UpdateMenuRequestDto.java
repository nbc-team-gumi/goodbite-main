package com.sparta.goodbite.domain.menu.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

@Getter
public class UpdateMenuRequestDto {

    @PositiveOrZero(message = "가격은 음수가 될 수 없습니다.")
    private Integer price;

    private String name;
    private String description;
}