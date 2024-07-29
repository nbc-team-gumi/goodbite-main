package com.sparta.goodbite.domain.operatinghour.service;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.detail.OperatingHourDuplicatedException;
import com.sparta.goodbite.exception.restaurant.RestaurantErrorCode;
import com.sparta.goodbite.exception.restaurant.detail.RestaurantNotAuthorizationException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperatingHourService {

    private final OperatingHourRepository operatingHourRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createOperatingHour(CreateOperatingHourRequestDto createOperatingHourRequestDto,
        EmailUserDetails userDetails) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createOperatingHourRequestDto.getRestaurantId());

        Owner owner = (Owner) userDetails.getUser();

        if (!Objects.equals(restaurant.getOwner(), owner)) {
            throw new RestaurantNotAuthorizationException(
                RestaurantErrorCode.RESTAURANT_NOT_AUTHORIZATION);
        }

        boolean isDuplicated = operatingHourRepository.existsByDayOfWeekAndRestaurant(
            createOperatingHourRequestDto.getDayOfWeek(),
            restaurant);
        if (isDuplicated) {
            throw new OperatingHourDuplicatedException(
                OperatingHourErrorCode.OPERATINGHOUR_DUPLICATED);
        }

        operatingHourRepository.save(createOperatingHourRequestDto.toEntity(restaurant));
    }

    @Transactional
    public void updateOperatingHour(Long operatingHourId,
        UpdateOperatingHourRequestDto updateOperatingHourRequestDto, EmailUserDetails userDetails) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);

        Owner owner = (Owner) userDetails.getUser();

        checkOwnerByOperatingHour(owner, operatingHour);

        operatingHour.update(updateOperatingHourRequestDto);
    }

    @Transactional
    public void deleteOperatingHour(Long operatingHourId, EmailUserDetails userDetails) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);

        Owner owner = (Owner) userDetails.getUser();

        checkOwnerByOperatingHour(owner, operatingHour);

        operatingHourRepository.delete(operatingHour);
    }

    private void checkOwnerByOperatingHour(Owner owner, OperatingHour operatingHour) {
        if (!Objects.equals(operatingHour.getRestaurant().getOwner(), owner)) {
            throw new RestaurantNotAuthorizationException(
                RestaurantErrorCode.RESTAURANT_NOT_AUTHORIZATION);
        }
    }
}