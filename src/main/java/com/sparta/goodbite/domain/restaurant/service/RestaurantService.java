package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.common.s3.service.S3Service;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantIdResponseDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantCreateFailedException;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantUpdateFailedException;
import java.util.List;
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

        try {
            restaurantRepository.save(restaurantRequestDto.toEntity(owner, restaurantImage));
        } catch (Exception e) {
            s3Service.deleteImageFromS3(restaurantImage);
            throw new RestaurantCreateFailedException(RestaurantErrorCode.RESTAURANT_CREATE_FAILED);
        }
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

        String originalImage = restaurant.getImageUrl();
        String restaurantImage =
            image != null && !image.isEmpty() ? s3Service.upload(image) : originalImage;
        try {
            restaurant.update(restaurantRequestDto, restaurantImage);
            s3Service.deleteImageFromS3(originalImage);
        } catch (Exception e) {
            s3Service.deleteImageFromS3(restaurantImage);
            throw new RestaurantUpdateFailedException(RestaurantErrorCode.RESTAURANT_UPDATE_FAILED);
        }
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
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}