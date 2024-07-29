package com.sparta.goodbite.domain.operatinghour.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.detail.OperatingHourDuplicatedException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperatingHourService {

    private final OperatingHourRepository operatingHourRepository;
    private final RestaurantRepository restaurantRepository;
    private final OwnerRepository ownerRepository;

    @Transactional
    public void createOperatingHour(CreateOperatingHourRequestDto createOperatingHourRequestDto,
        UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createOperatingHourRequestDto.getRestaurantId());

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        if (!Objects.equals(restaurant.getOwner().getId(), owner.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
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
        UpdateOperatingHourRequestDto updateOperatingHourRequestDto, UserCredentials user) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateOperatingHourOwnership(owner, operatingHour);

        operatingHour.update(updateOperatingHourRequestDto);
    }

    @Transactional
    public void deleteOperatingHour(Long operatingHourId, UserCredentials user) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateOperatingHourOwnership(owner, operatingHour);

        operatingHourRepository.delete(operatingHour);
    }

    private void validateOperatingHourOwnership(Owner owner, OperatingHour operatingHour) {
        if (!Objects.equals(operatingHour.getRestaurant().getOwner().getId(), owner.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}