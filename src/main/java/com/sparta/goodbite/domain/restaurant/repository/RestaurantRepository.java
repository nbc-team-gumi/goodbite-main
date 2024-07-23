package com.sparta.goodbite.domain.restaurant.repository;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}
