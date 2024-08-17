package com.sparta.goodbite.domain.menu.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.common.s3.service.S3Service;
import com.sparta.goodbite.domain.menu.dto.CreateMenuRequestDto;
import com.sparta.goodbite.domain.menu.dto.MenuResponseDto;
import com.sparta.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.menu.repository.MenuRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import com.sparta.goodbite.exception.menu.MenuErrorCode;
import com.sparta.goodbite.exception.menu.detail.MenuCreateFailedException;
import com.sparta.goodbite.exception.menu.detail.MenuUpdateFailedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;

    @Transactional
    public void createMenu(CreateMenuRequestDto createMenuRequestDto, UserCredentials user,
        MultipartFile image) {

        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());
        String imageUrl = s3Service.upload(image);

        try {
            menuRepository.save(createMenuRequestDto.toEntity(restaurant, imageUrl));
        } catch (Exception e) {
            s3Service.deleteImageFromS3(imageUrl);
            throw new MenuCreateFailedException(MenuErrorCode.MENU_CREATE_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public MenuResponseDto getMenu(Long menuId) {
        return MenuResponseDto.from(menuRepository.findByIdOrThrow(menuId));
    }

    @Transactional(readOnly = true)
    public List<MenuResponseDto> getAllMenusByRestaurantId(Long restaurantId) {
        restaurantRepository.findByIdOrThrow(restaurantId);
        return menuRepository.findAllByRestaurantId(restaurantId).stream()
            .map(MenuResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MenuResponseDto> getAllMenus() {
        return menuRepository.findAll().stream().map(MenuResponseDto::from).toList();
    }

    @Transactional
    public void updateMenu(Long menuId, UpdateMenuRequestDto updateMenuRequestDto,
        UserCredentials user, MultipartFile image) {

        Menu menu = menuRepository.findByIdOrThrow(menuId);
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());

        // 메뉴의 레스토랑과 소유자의 레스토랑이 일치하는지 검증
        validateMenuOwnership(menu, restaurant);

        String originalImageUrl = restaurant.getImageUrl();
        String newImageUrl =
            image != null && !image.isEmpty() ? s3Service.upload(image) : originalImageUrl;
        try {
            menu.update(updateMenuRequestDto, newImageUrl);
            s3Service.deleteImageFromS3(originalImageUrl);
        } catch (Exception e) {
            s3Service.deleteImageFromS3(newImageUrl);
            throw new MenuUpdateFailedException(MenuErrorCode.MENU_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteMenu(Long menuId, UserCredentials user) {
        Menu menu = menuRepository.findByIdOrThrow(menuId);
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());

        // 메뉴의 레스토랑과 소유자의 레스토랑이 일치하는지 검증
        validateMenuOwnership(menu, restaurant);

        menuRepository.delete(menu);
    }

    private void validateMenuOwnership(Menu menu, Restaurant restaurant) {
        if (!menu.getRestaurant().getId().equals(restaurant.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}