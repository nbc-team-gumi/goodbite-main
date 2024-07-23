package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
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

    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        return RestaurantResponseDto.from(restaurant);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getAllRestaurants() {

        List<Restaurant> restaurants = restaurantRepository.findAll();

        List<RestaurantResponseDto> restaurantResponseDtos = restaurants.stream()
            .map(RestaurantResponseDto::from)
            .toList();

        return restaurantResponseDtos;
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto restaurantRequestDto) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        restaurant.update(restaurantRequestDto);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        restaurantRepository.delete(restaurant);
    }
}
