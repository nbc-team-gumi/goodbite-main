package site.mygumi.goodbite.domain.operatinghour.service;

import site.mygumi.goodbite.common.UserCredentials;
import site.mygumi.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import site.mygumi.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import site.mygumi.goodbite.domain.owner.entity.Owner;
import site.mygumi.goodbite.domain.owner.repository.OwnerRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;
import site.mygumi.goodbite.exception.operatinghour.OperatingHourErrorCode;
import site.mygumi.goodbite.exception.operatinghour.detail.OperatingHourDuplicatedException;
import java.util.List;
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

    @Transactional(readOnly = true)
    public OperatingHourResponseDto getOperatingHour(Long operatingHourId) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);
        return OperatingHourResponseDto.from(operatingHour);
    }

    @Transactional(readOnly = true)
    public List<OperatingHourResponseDto> getAllOperatingHoursByRestaurantId(Long restaurantId) {

        restaurantRepository.findByIdOrThrow(restaurantId);
        List<OperatingHour> operatingHours = operatingHourRepository.findAllByRestaurantId(
            restaurantId);

        return operatingHours.stream().map(OperatingHourResponseDto::from).toList();
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