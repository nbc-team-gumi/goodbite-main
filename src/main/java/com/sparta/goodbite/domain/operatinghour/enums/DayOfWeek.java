package com.sparta.goodbite.domain.operatinghour.enums;

public enum DayOfWeek {

    MON("월"),
    TUE("화"),
    WED("수"),
    THU("목"),
    FRI("금"),
    SAT("토"),
    SUN("일");

    private final String dayOfWeek;

    DayOfWeek(String value) {
        this.dayOfWeek = value;
    }
}