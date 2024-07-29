package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantNotAuthorizationException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OperatingHourRepository operatingHourRepository;

    @Transactional
    public void createRestaurant(RestaurantRequestDto restaurantRequestDto,
        EmailUserDetails userDetails) {

        Owner owner = (Owner) userDetails.getUser();

        restaurantRepository.save(restaurantRequestDto.toEntity(owner));
    }

    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        return RestaurantResponseDto.from(restaurant);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getAllRestaurants() {

        List<Restaurant> restaurants = restaurantRepository.findAll();

        return restaurants.stream()
            .map(RestaurantResponseDto::from)
            .toList();
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto restaurantRequestDto,
        EmailUserDetails userDetails) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = (Owner) userDetails.getUser();

        checkOwnerByRestaurant(owner, restaurant);

        restaurant.update(restaurantRequestDto);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId, EmailUserDetails userDetails) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = (Owner) userDetails.getUser();

        checkOwnerByRestaurant(owner, restaurant);

        restaurantRepository.delete(restaurant);
    }

    @Transactional(readOnly = true)
    public List<OperatingHourResponseDto> getAllOperatingHoursByRestaurant(Long restaurantId) {

        restaurantRepository.findByIdOrThrow(restaurantId);
        List<OperatingHour> operatingHours = operatingHourRepository.findAllByRestaurantId(
            restaurantId);

        return operatingHours.stream()
            .map(OperatingHourResponseDto::from)
            .toList();
    }

    private void checkOwnerByRestaurant(Owner owner, Restaurant restaurant) {
        if (!Objects.equals(restaurant.getOwner(), owner)) {
            throw new RestaurantNotAuthorizationException(
                RestaurantErrorCode.RESTAURANT_NOT_AUTHORIZATION);
        }
    }
}
