package com.sparta.goodbite.domain.owner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateBusinessNumberRequestDto {

    @Pattern(regexp = "\\d{10}", message = "사업자 번호는 10자리 숫자여야 합니다.")
    @NotBlank(message = "사업자번호를 입력해주세요")
    private String newBusinessNumber;
}
