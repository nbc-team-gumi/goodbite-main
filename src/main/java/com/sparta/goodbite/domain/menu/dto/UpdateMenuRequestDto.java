package com.sparta.goodbite.domain.menu.dto;

import com.sparta.goodbite.domain.menu.dto.validation.constraint.AtLeastOneFieldConstraint;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

@Getter
@AtLeastOneFieldConstraint(message = "가격, 이름, 설명 중 적어도 하나는 비어 있지 않아야 합니다.")
public class UpdateMenuRequestDto {

    @PositiveOrZero(message = "가격은 음수가 될 수 없습니다.")
    private Integer price;

    private String name;
    private String description;
}