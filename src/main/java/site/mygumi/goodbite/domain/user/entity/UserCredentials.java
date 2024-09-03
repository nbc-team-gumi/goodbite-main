package site.mygumi.goodbite.domain.user.entity;

public interface UserCredentials {

    Long getId();

    String getEmail();

    String getPassword();

    boolean isCustomer();

    boolean isOwner();
}