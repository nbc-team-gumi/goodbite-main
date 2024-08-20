package com.sparta.goodbite.domain.restaurant.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.common.s3.service.S3Service;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantIdResponseDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.enums.Category;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantCreateFailedException;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantUpdateFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<RestaurantResponseDto> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable).map(RestaurantResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> getFilteredRestaurants(String sido, String sigungu,
        Category category, Double rating, Pageable pageable) {

        if (sido == null || sido.isEmpty()) {
            sigungu = null;
        }

        return restaurantRepository.findPageByFilters(sido, sigungu, category, rating, pageable)
            .map(RestaurantResponseDto::from);
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto restaurantRequestDto,
        UserCredentials user, MultipartFile image) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateRestaurantOwnership(owner, restaurant);

        String originalImage = restaurant.getImageUrl();
        String restaurantImage = originalImage;
        try {
            if (image != null && !image.isEmpty()) {
                restaurantImage = s3Service.upload(image);

                restaurant.update(restaurantRequestDto, restaurantImage);
                s3Service.deleteImageFromS3(originalImage);
            } else {
                restaurant.update(restaurantRequestDto, originalImage);
            }
        } catch (Exception e) {
            if (!restaurantImage.equals(originalImage)) {
                s3Service.deleteImageFromS3(restaurantImage);
            }
            throw new RestaurantUpdateFailedException(
                RestaurantErrorCode.RESTAURANT_UPDATE_FAILED);
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