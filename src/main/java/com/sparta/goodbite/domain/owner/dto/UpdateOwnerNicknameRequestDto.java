package com.sparta.goodbite.domain.owner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateOwnerNicknameRequestDto {

    @NotBlank(message = "새 닉네임을 입력해 주세요")
    @Pattern(regexp = "^(?![0-9]+$)[a-zA-Z가-힣0-9]{2,20}$", message = "닉네임은 한글, 영어, 숫자를 포함할 수 있으며 숫자만으로는 구성될 수 없습니다. (2자 이상 20자 이하)")
    private String newNickname;
}
