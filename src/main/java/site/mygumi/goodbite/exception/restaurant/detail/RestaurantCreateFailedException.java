package site.mygumi.goodbite.exception.restaurant.detail;

import site.mygumi.goodbite.exception.restaurant.RestaurantErrorCode;
import site.mygumi.goodbite.exception.restaurant.RestaurantException;

public class RestaurantCreateFailedException extends RestaurantException {

    public RestaurantCreateFailedException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}
