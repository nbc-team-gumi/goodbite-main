package com.sparta.goodbite.exception.restaurant.detail;

import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.RestaurantException;

public class RestaurantNotAuthorizationException extends RestaurantException {

    public RestaurantNotAuthorizationException(RestaurantErrorCode restaurantErrorCode) {
        super(restaurantErrorCode);
    }

}
