package com.sparta.goodbite.domain.operatinghour.service;

import com.sparta.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.detail.OpenTimeLaterThanCloseTimeException;
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

        if (createOperatingHourRequestDto.getOpenTime()
            .isAfter(createOperatingHourRequestDto.getCloseTime())) {
            throw new OpenTimeLaterThanCloseTimeException(
                OperatingHourErrorCode.OPERATINGHOUR_OPEN_AFTER_CLOSE);
        }
        operatingHourRepository.save(createOperatingHourRequestDto.toEntity(restaurant));
    }

    @Transactional
    public void updateOperatingHour(Long operatingHourId,
        UpdateOperatingHourRequestDto updateOperatingHourRequestDto) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);

        if (updateOperatingHourRequestDto.getOpenTime()
            .isAfter(updateOperatingHourRequestDto.getCloseTime())) {
            throw new OpenTimeLaterThanCloseTimeException(
                OperatingHourErrorCode.OPERATINGHOUR_OPEN_AFTER_CLOSE);
        }
        operatingHour.update(updateOperatingHourRequestDto);
    }

    @Transactional
    public void deleteOperatingHour(Long operatingHourId) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);
        operatingHourRepository.delete(operatingHour);
    }
}