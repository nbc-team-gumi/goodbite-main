package site.mygumi.goodbite.domain.menu.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.mygumi.goodbite.common.external.s3.service.S3Service;
import site.mygumi.goodbite.domain.menu.dto.CreateMenuRequestDto;
import site.mygumi.goodbite.domain.menu.dto.MenuResponseDto;
import site.mygumi.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import site.mygumi.goodbite.domain.menu.entity.Menu;
import site.mygumi.goodbite.domain.menu.repository.MenuRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;
import site.mygumi.goodbite.exception.menu.MenuErrorCode;
import site.mygumi.goodbite.exception.menu.detail.MenuCreateFailedException;
import site.mygumi.goodbite.exception.menu.detail.MenuUpdateFailedException;

/**
 * 메뉴 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 메뉴 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @author sillysillyman
 */
@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;

    /**
     * 새로운 메뉴를 생성합니다.
     *
     * @param createMenuRequestDto 메뉴 생성 요청 정보가 담긴 DTO
     * @param user                 메뉴를 생성하는 사용자의 인증 정보
     * @param image                메뉴 이미지 파일
     * @throws MenuCreateFailedException 메뉴 생성에 실패할 경우 발생합니다.
     */
    @Transactional
    public void createMenu(CreateMenuRequestDto createMenuRequestDto, UserCredentials user,
        MultipartFile image) {
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());

        String menuImage = s3Service.upload(image);
        try {
            menuRepository.save(createMenuRequestDto.toEntity(restaurant, menuImage));
        } catch (Exception e) {
            s3Service.deleteImageFromS3(menuImage);
            throw new MenuCreateFailedException(MenuErrorCode.MENU_CREATE_FAILED);
        }
    }

    /**
     * 주어진 메뉴 ID에 해당하는 메뉴를 조회합니다.
     *
     * @param menuId 조회할 메뉴의 ID
     * @return 조회된 메뉴 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public MenuResponseDto getMenu(Long menuId) {
        return MenuResponseDto.from(menuRepository.findByIdOrThrow(menuId));
    }

    /**
     * 특정 레스토랑의 모든 메뉴를 페이지 형태로 조회합니다.
     *
     * @param restaurantId 레스토랑의 ID
     * @param pageable     페이지 정보
     * @return 특정 레스토랑의 메뉴 리스트 페이지
     */
    @Transactional(readOnly = true)
    public Page<MenuResponseDto> getAllMenusByRestaurantId(Long restaurantId, Pageable pageable) {
        restaurantRepository.validateById(restaurantId);
        return menuRepository.findPageByRestaurantId(restaurantId, pageable)
            .map(MenuResponseDto::from);
    }

    /**
     * 모든 메뉴를 조회합니다.
     *
     * @return 모든 메뉴를 담은 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MenuResponseDto> getAllMenus() {
        return menuRepository.findAll().stream().map(MenuResponseDto::from).toList();
    }

    /**
     * 메뉴 정보를 업데이트합니다.
     *
     * @param menuId               업데이트할 메뉴의 ID
     * @param updateMenuRequestDto 메뉴 업데이트 요청 정보가 담긴 DTO
     * @param user                 메뉴를 업데이트하는 사용자의 인증 정보
     * @param image                메뉴의 새 이미지 파일
     * @throws MenuUpdateFailedException 메뉴 업데이트에 실패할 경우 발생합니다.
     */
    @Transactional
    public void updateMenu(Long menuId, UpdateMenuRequestDto updateMenuRequestDto,
        UserCredentials user, MultipartFile image) {

        Menu menu = menuRepository.findByIdOrThrow(menuId);
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());

        // 메뉴의 레스토랑과 소유자의 레스토랑이 일치하는지 검증
        validateMenuOwnership(menu, restaurant);

        String originalImage = menu.getImageUrl();
        String menuImage = originalImage;
        try {
            if (image != null) {
                menuImage = s3Service.upload(image);

                menu.update(updateMenuRequestDto, menuImage);
                s3Service.deleteImageFromS3(originalImage);
            } else {
                menu.update(updateMenuRequestDto, originalImage);
            }
        } catch (Exception e) {
            if (!menuImage.equals(originalImage)) {
                s3Service.deleteImageFromS3(menuImage);
            }
            throw new MenuUpdateFailedException(
                MenuErrorCode.MENU_UPDATE_FAILED);
        }
    }

    /**
     * 메뉴를 삭제합니다.
     *
     * @param menuId 삭제할 메뉴의 ID
     * @param user   메뉴를 삭제하는 사용자의 인증 정보
     */
    @Transactional
    public void deleteMenu(Long menuId, UserCredentials user) {
        Menu menu = menuRepository.findByIdOrThrow(menuId);
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());

        // 메뉴의 레스토랑과 소유자의 레스토랑이 일치하는지 검증
        validateMenuOwnership(menu, restaurant);

        menuRepository.delete(menu);
        s3Service.deleteImageFromS3(menu.getImageUrl());
    }

    /**
     * 메뉴가 소유자의 레스토랑에 속하는지 검증합니다.
     *
     * @param menu       검증할 메뉴
     * @param restaurant 소유자의 레스토랑
     * @throws AuthException 메뉴가 소유자의 레스토랑에 속하지 않을 경우 발생합니다.
     */
    private void validateMenuOwnership(Menu menu, Restaurant restaurant) {
        if (!menu.getRestaurant().getId().equals(restaurant.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}