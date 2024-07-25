package com.sparta.goodbite.domain.menu.dto;

import lombok.Getter;

@Getter
public class UpdateMenuRequestDto {

    private Integer price;
    private String name;
    private String description;
}