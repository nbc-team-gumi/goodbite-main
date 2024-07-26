package com.sparta.goodbite.domain.menu.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createMenu(
        @Valid @RequestBody CreateMenuRequestDto createMenuRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        menuService.createMenu(createMenuRequestDto, userDetails.getUser());
        return ResponseUtil.createOk();
    }

    @PreAuthorize("hasAnyRole('OWNER', 'CUSTOMER')")
    @GetMapping("/{menuId}")
    public ResponseEntity<DataResponseDto<MenuResponseDto>> getMenu(@PathVariable Long menuId,
        @AuthenticationPrincipal EmailUserDetails _userDetails) {

        return ResponseUtil.findOk(menuService.getMenu(menuId));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{menuId}")
    public ResponseEntity<MessageResponseDto> updateMenu(
        @PathVariable Long menuId, @Valid @RequestBody UpdateMenuRequestDto updateMenuRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        menuService.updateMenu(menuId, updateMenuRequestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{menuId}")
    public ResponseEntity<MessageResponseDto> deleteMenu(@PathVariable Long menuId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        menuService.deleteMenu(menuId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}