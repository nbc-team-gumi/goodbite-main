package site.mygumi.goodbite.domain.restaurant.exception.detail;

import site.mygumi.goodbite.domain.restaurant.exception.RestaurantErrorCode;
import site.mygumi.goodbite.domain.restaurant.exception.RestaurantException;

public class RestaurantUpdateFailedException extends RestaurantException {

    public RestaurantUpdateFailedException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}
