package com.sparta.goodbite.domain.menu.service;

import com.sparta.goodbite.domain.menu.dto.CreateMenuRequestDto;
import com.sparta.goodbite.domain.menu.dto.MenuResponseDto;
import com.sparta.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.menu.repository.MenuRepository;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
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
    public void createMenu(CreateMenuRequestDto createMenuRequestDto) { // User user
        menuRepository.save(createMenuRequestDto.toEntity());
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

    @Transactional
    public void updateMenu(Long menuId, UpdateMenuRequestDto updateMenuRequestDto) {
        Menu menu = menuRepository.findByIdOrThrow(menuId);
        menu.update(updateMenuRequestDto);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        menuRepository.delete(menuRepository.findByIdOrThrow(menuId));
    }
}