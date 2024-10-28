package site.mygumi.goodbite.domain.user.entity;

/**
 * 두 가지 사용자 클래스(Customer, Owner)에서 공통적으로 사용되는 메서드들 입니다.
 *
 * @author a-white-bit
 */
public interface UserCredentials {

    Long getId();

    String getEmail();

    String getPassword();

    boolean isCustomer();

    boolean isOwner();
}