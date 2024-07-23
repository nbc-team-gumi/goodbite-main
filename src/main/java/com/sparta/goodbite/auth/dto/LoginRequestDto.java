package com.sparta.goodbite.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {

    private String email;
    private String password;
    private String role;
}
