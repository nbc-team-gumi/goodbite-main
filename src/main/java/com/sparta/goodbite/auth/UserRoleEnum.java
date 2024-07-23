package com.sparta.goodbite.auth;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    CUSTOMER(Authority.CUSTOMER),   // 손님 사용자 권한
    OWNER(Authority.OWNER),         // 식당 사용자 권한
    ADMIN(Authority.ADMIN);         // 관리자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public static class Authority {

        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}