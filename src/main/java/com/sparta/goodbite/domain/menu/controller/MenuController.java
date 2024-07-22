package com.sparta.goodbite.domain.menu.controller;

import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.menu.dto.CreateMenuRequestDto;
import com.sparta.goodbite.domain.menu.dto.MenuResponseDto;
import com.sparta.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import com.sparta.goodbite.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/menus")
@RestController
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createMenu(
        @RequestBody @Valid CreateMenuRequestDto createMenuRequestDto) { // @AuthenticationPricipal UserDetailsImpl userDetails

        menuService.createMenu(
            createMenuRequestDto); // menuService.createMenu(createMenuRequestDto, user);
        return ResponseUtil.createOk();
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<DataResponseDto<MenuResponseDto>> getMenu(@PathVariable Long menuId) {
        return ResponseUtil.findOk(menuService.getMenu(menuId));
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<MessageResponseDto> updateMenu(
        @PathVariable Long menuId, @RequestBody UpdateMenuRequestDto updateMenuRequestDto) {

        menuService.updateMenu(menuId, updateMenuRequestDto);
        return ResponseUtil.updateOk();
    }

    @DeleteMapping("{menuId}")
    public ResponseEntity<MessageResponseDto> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseUtil.deleteOk();
    }
}