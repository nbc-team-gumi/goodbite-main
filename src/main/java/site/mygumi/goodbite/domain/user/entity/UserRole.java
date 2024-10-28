package site.mygumi.goodbite.domain.user.entity;

import lombok.Getter;

/**
 * 사용자 역할 종류
 * <p>
 * CUSTOMER: 손님, OWNER: 가게/식당 주인, ADMIN: 관리자
 * </p>
 *
 * @author a-white-bit
 */
@Getter
public enum UserRole {
    CUSTOMER("ROLE_CUSTOMER"),
    OWNER("ROLE_OWNER"),
    ADMIN("ROLE_ADMIN");
    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }
}