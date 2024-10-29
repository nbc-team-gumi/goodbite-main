package site.mygumi.goodbite.domain.restaurant.exception.detail;

import site.mygumi.goodbite.domain.restaurant.exception.RestaurantErrorCode;
import site.mygumi.goodbite.domain.restaurant.exception.RestaurantException;

public class RestaurantNotFoundException extends RestaurantException {

    public RestaurantNotFoundException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}