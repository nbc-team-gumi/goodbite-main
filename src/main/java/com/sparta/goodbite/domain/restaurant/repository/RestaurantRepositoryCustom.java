package com.sparta.goodbite.domain.restaurant.repository;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {

    Page<Restaurant> findPageByFilters(String sido, String sigungu, Category category,
        Double rating, Pageable pageable);
}