package com.sparta.goodbite.exception.restaurant.detail;

import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.RestaurantException;

public class RestaurantNotFoundException extends RestaurantException {

    public RestaurantNotFoundException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }
}
