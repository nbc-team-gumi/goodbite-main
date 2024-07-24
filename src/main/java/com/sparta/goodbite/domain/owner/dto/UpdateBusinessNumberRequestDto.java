package com.sparta.goodbite.domain.owner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateBusinessNumberRequestDto {

    @NotBlank
    private String newBusinessNumber;
}
