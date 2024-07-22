package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        restaurantRepository.save(restaurantRequestDto.toEntity());
    }
}
