package com.sparta.goodbite.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.operatinghour.dto.validation.contraint.LocalTimeFormatConstraint;
import com.sparta.goodbite.domain.reservation.dto.validation.constraint.LocalDateFormatConstraint;
import com.sparta.goodbite.domain.reservation.dto.validation.constraint.ReservationDateConstraint;
import com.sparta.goodbite.domain.reservation.dto.validation.constraint.ReservationTimeConstraint;
import com.sparta.goodbite.domain.reservation.dto.validation.constraint.TimeIntervalConstraint;
import com.sparta.goodbite.domain.reservation.entity.Reservation;
import com.sparta.goodbite.domain.reservation.entity.ReservationMenu;
import com.sparta.goodbite.domain.reservation.entity.ReservationStatus;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateReservationRequestDto {

    @NotNull(message = "식당 ID를 입력해 주세요.")
    private Long restaurantId;

    @NotNull(message = "예약 날짜를 입력해 주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @LocalDateFormatConstraint
    @ReservationDateConstraint
    private LocalDate date;

    @NotNull(message = "예약 시간을 입력해 주세요.")
    @JsonFormat(pattern = "HH:mm")
    @LocalTimeFormatConstraint
    @TimeIntervalConstraint
    @ReservationTimeConstraint
    private LocalTime time;

    private String requirement;

    @NotNull(message = "인원 수를 입력해 주세요.")
    private int partySize;

    private List<MenuItemDto> menuItems;

    public Reservation toEntity(Customer customer, Restaurant restaurant) {
        return Reservation.builder()
            .customer(customer)
            .restaurant(restaurant)
            .date(date)
            .time(time)
            .requirement(requirement)
            .partySize(partySize)
            .status(ReservationStatus.PENDING)
            .build();
    }

    public Reservation toEntity(Customer customer, Restaurant restaurant,
        List<ReservationMenu> reservationMenus) {
        return Reservation.builder()
            .customer(customer)
            .restaurant(restaurant)
            .reservationMenus(reservationMenus)
            .date(date)
            .time(time)
            .requirement(requirement)
            .partySize(partySize)
            .status(ReservationStatus.PENDING)
            .build();
    }
}