package site.mygumi.goodbite.domain.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.mygumi.goodbite.common.external.s3.service.S3Service;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantIdResponseDto;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.enums.Category;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;
import site.mygumi.goodbite.exception.restaurant.RestaurantErrorCode;
import site.mygumi.goodbite.exception.restaurant.detail.RestaurantCreateFailedException;
import site.mygumi.goodbite.exception.restaurant.detail.RestaurantUpdateFailedException;

/**
 * 레스토랑 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 레스토랑 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @author sillysillyman
 */
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OwnerRepository ownerRepository;
    private final S3Service s3Service;

    /**
     * 새로운 레스토랑을 생성합니다.
     *
     * @param restaurantRequestDto 레스토랑 생성 요청 정보가 담긴 DTO
     * @param user                 레스토랑을 생성하는 사용자의 인증 정보
     * @param image                레스토랑 이미지 파일
     * @throws RestaurantCreateFailedException 레스토랑 생성에 실패할 경우 발생합니다.
     */
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

    /**
     * 특정 ID의 레스토랑 정보를 조회합니다.
     *
     * @param restaurantId 조회할 레스토랑의 ID
     * @return 조회된 레스토랑 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        return RestaurantResponseDto.from(restaurant);
    }

    /**
     * 현재 사용자의 레스토랑 ID를 조회합니다.
     *
     * @param user 현재 사용자의 인증 정보
     * @return 사용자의 레스토랑 ID를 담은 DTO
     */
    @Transactional(readOnly = true)
    public RestaurantIdResponseDto getMyRestaurant(UserCredentials user) {
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());
        return new RestaurantIdResponseDto(restaurant.getId());
    }

    /**
     * 모든 레스토랑을 페이지로 조회합니다.
     *
     * @param pageable 페이지 정보
     * @return 레스토랑 리스트 페이지
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable).map(RestaurantResponseDto::from);
    }

    /**
     * 필터링된 레스토랑 리스트를 페이지로 조회합니다.
     *
     * @param sido     시/도
     * @param sigungu  시/군/구
     * @param category 카테고리
     * @param rating   최소 평점
     * @param pageable 페이지 정보
     * @return 필터링된 레스토랑 리스트 페이지
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> getFilteredRestaurants(String sido, String sigungu,
        Category category, Double rating, Pageable pageable) {

        return restaurantRepository.findPageByFilters(sido, sigungu, category, rating, pageable)
            .map(RestaurantResponseDto::from);
    }

    /**
     * 특정 ID의 레스토랑 정보를 업데이트합니다.
     *
     * @param restaurantId         업데이트할 레스토랑의 ID
     * @param restaurantRequestDto 레스토랑 업데이트 요청 정보가 담긴 DTO
     * @param user                 레스토랑을 업데이트하는 사용자의 인증 정보
     * @param image                새 레스토랑 이미지 파일
     * @throws RestaurantUpdateFailedException 레스토랑 업데이트에 실패할 경우 발생합니다.
     */
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

    /**
     * 특정 ID의 레스토랑을 삭제합니다.
     *
     * @param restaurantId 삭제할 레스토랑의 ID
     * @param user         레스토랑을 삭제하는 사용자의 인증 정보
     */
    @Transactional
    public void deleteRestaurant(Long restaurantId, UserCredentials user) {

        Restaurant restaurant = restaurantRepository.findByIdOrThrow(restaurantId);
        Owner owner = ownerRepository.findByIdOrThrow(user.getId());

        validateRestaurantOwnership(owner, restaurant);

        restaurantRepository.delete(restaurant);
        s3Service.deleteImageFromS3(restaurant.getImageUrl());
    }

    /**
     * 레스토랑의 소유자가 현재 사용자와 일치하는지 검증합니다.
     *
     * @param owner      현재 사용자
     * @param restaurant 검증할 레스토랑
     * @throws AuthException 사용자가 해당 레스토랑의 소유자가 아닌 경우 발생합니다.
     */
    private void validateRestaurantOwnership(Owner owner, Restaurant restaurant) {
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}