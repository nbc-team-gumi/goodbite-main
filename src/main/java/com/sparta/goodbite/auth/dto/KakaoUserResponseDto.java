package com.sparta.goodbite.auth.dto;

public record KakaoUserResponseDto(Long id, String nickname, String email) {

    public static KakaoUserResponseDto from(Long id, String nickname, String email) {
        return new KakaoUserResponseDto(id, nickname, email);
    }
}
