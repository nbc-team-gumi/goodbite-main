package site.mygumi.goodbite.domain.restaurant.exception;

import lombok.Getter;

@Getter
public class RestaurantException extends RuntimeException {

    private final RestaurantErrorCode restaurantErrorCode;

    public RestaurantException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode.getMessage());
        this.restaurantErrorCode = restaurantErrorCode;
    }
}