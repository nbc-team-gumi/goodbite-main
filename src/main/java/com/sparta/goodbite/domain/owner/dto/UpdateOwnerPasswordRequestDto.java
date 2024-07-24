package com.sparta.goodbite.domain.owner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateOwnerPasswordRequestDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
        message = "최소 8자 이상, 15자 이하의 알파벳 대소문자, 숫자, 특수문자로 구성되어야 합니다.")
    private String newPassword;
}
