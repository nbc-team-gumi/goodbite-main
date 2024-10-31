package site.mygumi.goodbite.domain.operatinghour.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import site.mygumi.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourErrorCode;
import site.mygumi.goodbite.domain.operatinghour.exception.detail.OperatingHourDuplicatedException;
import site.mygumi.goodbite.domain.operatinghour.repository.OperatingHourRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;


/**
 * 영업시간 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 영업시간 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @author haeuni00
 */
@Service
@RequiredArgsConstructor
public class OperatingHourService {

    private final OperatingHourRepository operatingHourRepository;
    private final RestaurantRepository restaurantRepository;
    private final OwnerRepository ownerRepository;

    /**
     * 영업시간을 생성합니다.
     *
     * @param createOperatingHourRequestDto 영업시간 생성 요청 정보
     * @param user                          영업시간 생성을 요청하는 사용자 정보
     * @throws AuthException                    영업시간을 생성하려는 레스토랑의 주인이 아닐 경우 발생합니다.
     * @throws OperatingHourDuplicatedException 해당 요일에 생성된 영업시간이 있을 경우 발생합니다.
     */
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

    /**
     * 영업시간 ID로 해당 영업시간을 조회합니다.
     *
     * @param operatingHourId 조회할 영업시간의 ID
     * @return 해당 영업시간 정보를 포함한 DTO
     */
    @Transactional(readOnly = true)
    public OperatingHourResponseDto getOperatingHour(Long operatingHourId) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);
        return OperatingHourResponseDto.from(operatingHour);
    }

    /**
     * 레스토랑 ID로 해당 레스토랑의 모든 영업시간을 조회합니다.
     *
     * @param restaurantId 영업시간을 조회할 레스토랑의 ID
     * @return 해당 가게의 모든 영업시간 정보가 담긴 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<OperatingHourResponseDto> getAllOperatingHoursByRestaurantId(Long restaurantId) {

        restaurantRepository.findByIdOrThrow(restaurantId);
        List<OperatingHour> operatingHours = operatingHourRepository.findAllByRestaurantId(
            restaurantId);

        return operatingHours.stream().map(OperatingHourResponseDto::from).toList();
    }

    /**
     * 영업시간을 수정합니다.
     *
     * @param operatingHourId               수정할 영업시간의 ID
     * @param updateOperatingHourRequestDto 영업시간 수정 요청 정보
     * @param user                          영업시간 수정을 요청하는 사용자 정보
     */
    @Transactional
    public void updateOperatingHour(Long operatingHourId,
        UpdateOperatingHourRequestDto updateOperatingHourRequestDto, UserCredentials user) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateOperatingHourOwnership(owner, operatingHour);

        operatingHour.update(updateOperatingHourRequestDto);
    }

    /**
     * 영업시간을 삭제합니다.
     *
     * @param operatingHourId 삭제할 영업시간의 ID
     * @param user            영업시간을 삭제하는 사용자 정보
     */
    @Transactional
    public void deleteOperatingHour(Long operatingHourId, UserCredentials user) {

        OperatingHour operatingHour = operatingHourRepository.findByIdOrThrow(operatingHourId);

        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateOperatingHourOwnership(owner, operatingHour);

        operatingHourRepository.delete(operatingHour);
    }

    /**
     * 영업시간의 주인인지 검증합니다.
     *
     * @param owner         검증할 사용자 정보
     * @param operatingHour 검증할 영업시간 정보
     * @throws AuthException 사용자가 영업시간의 주인이 아닐 경우 발생합니다.
     */
    private void validateOperatingHourOwnership(Owner owner, OperatingHour operatingHour) {
        if (!Objects.equals(operatingHour.getRestaurant().getOwner().getId(), owner.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}