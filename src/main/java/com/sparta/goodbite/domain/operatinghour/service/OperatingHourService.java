package com.sparta.goodbite.domain.operatinghour.service;

import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
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
    public void updateOperatingHour(Long operatingHourId,
        OperatingHourRequestDto operatingHourRequestDto) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);
        operatingHour.update(operatingHourRequestDto);
    }

    @Transactional
    public void deleteOperatingHour(Long operatingHourId) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);
        operatingHourRepository.delete(operatingHour);
    }

    @Transactional(readOnly = true)
    public List<OperatingHourResponseDto> getAllOperatingHoursByRestaurant(Long restaurantId) {

        List<OperatingHour> operatingHours = operatingHourRepository.findAllByRestaurantId(
            restaurantId);

        return operatingHours.stream()
            .map(OperatingHourResponseDto::from)
            .toList();
    }
}