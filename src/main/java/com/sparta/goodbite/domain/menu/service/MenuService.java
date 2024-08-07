package com.sparta.goodbite.domain.menu.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.menu.dto.CreateMenuRequestDto;
import com.sparta.goodbite.domain.menu.dto.MenuResponseDto;
import com.sparta.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.menu.repository.MenuRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createMenu(CreateMenuRequestDto createMenuRequestDto, UserCredentials user) {
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());
        menuRepository.save(createMenuRequestDto.toEntity(restaurant));
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
        UserCredentials user) {

        Menu menu = menuRepository.findByIdOrThrow(menuId);
        Restaurant restaurant = restaurantRepository.findByOwnerIdOrThrow(user.getId());

        // 메뉴의 레스토랑과 소유자의 레스토랑이 일치하는지 검증
        validateMenuOwnership(menu, restaurant);

        menu.update(updateMenuRequestDto);
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