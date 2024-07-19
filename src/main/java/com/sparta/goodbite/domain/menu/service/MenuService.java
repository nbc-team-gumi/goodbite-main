package com.sparta.goodbite.domain.menu.service;

import com.sparta.goodbite.domain.menu.dto.CreateMenuRequestDto;
import com.sparta.goodbite.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public void createMenu(CreateMenuRequestDto createMenuRequestDto) { // User user
        menuRepository.save(createMenuRequestDto.toEntity());
    }
}