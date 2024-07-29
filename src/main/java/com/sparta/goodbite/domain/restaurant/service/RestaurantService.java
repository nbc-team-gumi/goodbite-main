package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
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
    private final OwnerRepository ownerRepository;

    @Transactional
    public void createRestaurant(RestaurantRequestDto restaurantRequestDto, UserCredentials user) {

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

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
        UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        checkOwnerByRestaurant(owner, restaurant);

        restaurant.update(restaurantRequestDto);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId, UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

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
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}