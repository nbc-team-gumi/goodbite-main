package site.mygumi.goodbite.domain.restaurant.repository;

import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {

    Page<Restaurant> findPageByFilters(String sido, String sigungu, Category category,
        Double rating, Pageable pageable);
}