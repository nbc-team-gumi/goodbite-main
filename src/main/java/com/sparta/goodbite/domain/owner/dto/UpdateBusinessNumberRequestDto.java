package com.sparta.goodbite.domain.owner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateBusinessNumberRequestDto {

    @NotBlank(message = "사업자번호를 입력해주세요")
    private String newBusinessNumber;
}
