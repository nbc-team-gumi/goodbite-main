package site.mygumi.goodbite.exception.restaurant.detail;

import site.mygumi.goodbite.exception.restaurant.RestaurantErrorCode;
import site.mygumi.goodbite.exception.restaurant.RestaurantException;

public class RestaurantNotFoundException extends RestaurantException {

    public RestaurantNotFoundException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}