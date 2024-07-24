package com.sparta.goodbite.domain.operatinghour.service;

import com.sparta.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.detail.OperatingHourDuplicatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperatingHourService {

    private final OperatingHourRepository operatingHourRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createOperatingHour(CreateOperatingHourRequestDto createOperatingHourRequestDto) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createOperatingHourRequestDto.getRestaurantId());

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
        UpdateOperatingHourRequestDto updateOperatingHourRequestDto) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);
        operatingHour.update(updateOperatingHourRequestDto);
    }

    @Transactional
    public void deleteOperatingHour(Long operatingHourId) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);
        operatingHourRepository.delete(operatingHour);
    }
}