package com.sparta.goodbite.exception.restaurant.detail;

import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.RestaurantException;

public class RestaurantCreateFailedException extends RestaurantException {

    public RestaurantCreateFailedException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}
