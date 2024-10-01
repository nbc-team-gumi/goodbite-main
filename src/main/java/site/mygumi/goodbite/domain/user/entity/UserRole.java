package site.mygumi.goodbite.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    CUSTOMER("ROLE_CUSTOMER"),   // 손님 사용자 권한
    OWNER("ROLE_OWNER"),         // 식당 사용자 권한
    ADMIN("ROLE_ADMIN");         // 관리자 권한

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }
}