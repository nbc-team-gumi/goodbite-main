package com.sparta.goodbite.exception.restaurant.detail;

import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.RestaurantException;

public class RestaurantUpdateFailedException extends RestaurantException {

    public RestaurantUpdateFailedException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}
