package com.sparta.goodbite.common;

public interface UserCredentials {

    Long getId();

    String getEmail();

    String getPassword();

    boolean isCustomer();

    boolean isOwner();
}
