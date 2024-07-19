package com.sparta.goodbite.domain.menu.controller;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/menus")
@RestController
public class MenuController {

    private final MenuService menuService;

    public ResponseEntity<MessageResponseDto> createMenu() {
        return ResponseUtil.createOk();
    }
}