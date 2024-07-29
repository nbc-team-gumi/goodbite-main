package com.sparta.goodbite.domain.restaurant.repository;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    default Restaurant findByIdOrThrow(Long restaurantId) {
        return findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(
            RestaurantErrorCode.RESTAURANT_NOT_FOUND));
    }
}