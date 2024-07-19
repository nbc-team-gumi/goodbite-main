package com.sparta.goodbite.domain.menu.controller;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.menu.dto.CreateMenuRequestDto;
import com.sparta.goodbite.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}