package com.sparta.goodbite.domain.menu.repository;

import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.exception.menu.MenuErrorCode;
import com.sparta.goodbite.exception.menu.detail.MenuNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    default Menu findByIdOrThrow(Long menuId) {
        return findById(menuId).orElseThrow(
            () -> new MenuNotFoundException(MenuErrorCode.MENU_NOT_FOUND));
    }
}