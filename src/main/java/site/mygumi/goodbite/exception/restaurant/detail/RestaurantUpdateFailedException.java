package site.mygumi.goodbite.exception.restaurant.detail;

import site.mygumi.goodbite.exception.restaurant.RestaurantErrorCode;
import site.mygumi.goodbite.exception.restaurant.RestaurantException;

public class RestaurantUpdateFailedException extends RestaurantException {

    public RestaurantUpdateFailedException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}
