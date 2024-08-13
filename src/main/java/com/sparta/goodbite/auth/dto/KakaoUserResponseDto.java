package com.sparta.goodbite.auth.dto;

public record KakaoUserResponseDto(String nickname, String email) {

    public static KakaoUserResponseDto from(String nickname, String email) {
        return new KakaoUserResponseDto(nickname, email);
    }

    public String getEmail() {
        return this.email;
    }
}
