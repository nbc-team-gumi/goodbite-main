package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantIdResponseDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import com.sparta.goodbite.s3.S3Service;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OwnerRepository ownerRepository;
    private final S3Service s3Service;

    @Transactional
    public void createRestaurant(RestaurantRequestDto restaurantRequestDto, UserCredentials user,
        MultipartFile image) {

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());
        String restaurantImage = s3Service.upload(image);
        restaurantRepository.save(restaurantRequestDto.toEntity(owner, restaurantImage));
    }

    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        return RestaurantResponseDto.from(restaurant);
    }

    @Transactional(readOnly = true)
    public RestaurantIdResponseDto getMyRestaurant(UserCredentials user) {
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());
        return new RestaurantIdResponseDto(restaurant.getId());
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getAllRestaurants() {
        return restaurantRepository.findAll().stream().map(RestaurantResponseDto::from).toList();
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto restaurantRequestDto,
        UserCredentials user, MultipartFile image) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateRestaurantOwnership(owner, restaurant);

        s3Service.deleteImageFromS3(restaurant.getImageUrl());
        String restaurantImage = s3Service.upload(image);

        restaurant.update(restaurantRequestDto, restaurantImage);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId, UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateRestaurantOwnership(owner, restaurant);

        restaurantRepository.delete(restaurant);
        s3Service.deleteImageFromS3(restaurant.getImageUrl());
    }

    private void validateRestaurantOwnership(Owner owner, Restaurant restaurant) {
        if (!Objects.equals(restaurant.getOwner(), owner)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}