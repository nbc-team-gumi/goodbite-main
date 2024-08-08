package com.sparta.goodbite.domain.restaurant.enums;

public enum Category {

    KOREAN("한식"),
    JAPANESE("일식"),
    CHINESE("중식"),
    WESTERN("양식"),
    ASIAN("아시안"),
    BUNSIK("분식"),
    PIZZA("피자"),
    CHICKEN("치킨"),
    BURGER("버거"),
    CAFE("카페");

    private final String category;

    Category(String value) {
        this.category = value;
    }
}
