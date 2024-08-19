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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        String menuImage = s3Service.upload(image);
        try {
            menuRepository.save(createMenuRequestDto.toEntity(restaurant, menuImage));
        } catch (Exception e) {
            s3Service.deleteImageFromS3(menuImage);
            throw new MenuCreateFailedException(MenuErrorCode.MENU_CREATE_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public MenuResponseDto getMenu(Long menuId) {
        return MenuResponseDto.from(menuRepository.findByIdOrThrow(menuId));
    }

    @Transactional(readOnly = true)
    public Page<MenuResponseDto> getAllMenusByRestaurantId(Long restaurantId, Pageable pageable) {
        restaurantRepository.validateById(restaurantId);
        return menuRepository.findAllByRestaurantId(restaurantId, pageable)
            .map(MenuResponseDto::from);
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

    @Transactional
    public void deleteMenu(Long menuId, UserCredentials user) {
        Menu menu = menuRepository.findByIdOrThrow(menuId);
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());

        // 메뉴의 레스토랑과 소유자의 레스토랑이 일치하는지 검증
        validateMenuOwnership(menu, restaurant);

        menuRepository.delete(menu);
        s3Service.deleteImageFromS3(menu.getImageUrl());
    }

    private void validateMenuOwnership(Menu menu, Restaurant restaurant) {
        if (!menu.getRestaurant().getId().equals(restaurant.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}