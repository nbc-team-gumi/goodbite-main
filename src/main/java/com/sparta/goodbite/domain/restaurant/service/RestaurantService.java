package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
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

    @Transactional
    public RestaurantResponseDto getRestaurant(Long restaurantId) {

        Restaurant restaurant = findRestaurant(restaurantId);
        return new RestaurantResponseDto(restaurant);
    }

    @Transactional
    public List<RestaurantResponseDto> getRestaurants() {

        List<Restaurant> restaurants = restaurantRepository.findAll();

        List<RestaurantResponseDto> restaurantResponseDtos = restaurants.stream()
            .map(RestaurantResponseDto::new)
            .collect(Collectors.toList());

        return restaurantResponseDtos;
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto restaurantRequestDto) {

        Restaurant restaurant = findRestaurant(restaurantId);
        restaurant.update(restaurantRequestDto);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {

        Restaurant restaurant = findRestaurant(restaurantId);
        restaurantRepository.delete(restaurant);
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(
                RestaurantErrorCode.RESTAURANT_NOT_FOUND));
    }


}
