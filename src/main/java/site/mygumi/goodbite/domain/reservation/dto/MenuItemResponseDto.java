package site.mygumi.goodbite.domain.reservation.dto;

import site.mygumi.goodbite.domain.reservation.entity.ReservationMenu;

public record MenuItemResponseDto(Long menuId, String name, int quantity) {

    public static MenuItemResponseDto from(ReservationMenu reservationMenu) {
        return new MenuItemResponseDto(
            reservationMenu.getMenu().getId(),
            reservationMenu.getMenu().getName(),
            reservationMenu.getQuantity()
        );
    }
}