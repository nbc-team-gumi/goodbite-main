package site.mygumi.goodbite.domain.restaurant.exception.detail;

import site.mygumi.goodbite.domain.restaurant.exception.RestaurantErrorCode;
import site.mygumi.goodbite.domain.restaurant.exception.RestaurantException;

public class RestaurantCreateFailedException extends RestaurantException {

    public RestaurantCreateFailedException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}
