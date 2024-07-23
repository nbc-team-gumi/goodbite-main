package com.sparta.goodbite.domain.operatinghour.service;

import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperatingHourService {

    private final OperatingHourRepository operatingHourRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createOperatingHour(OperatingHourRequestDto operatingHourRequestDto) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            operatingHourRequestDto.getRestaurantId());
        operatingHourRepository.save(operatingHourRequestDto.toEntity(restaurant));
    }

    @Transactional
    public void updateOperatingHour(Long operationHourId,
        OperatingHourRequestDto operatingHourRequestDto) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operationHourId);
        operatingHour.update(operatingHourRequestDto);

    }
}