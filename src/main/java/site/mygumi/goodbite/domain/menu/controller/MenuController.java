package site.mygumi.goodbite.domain.menu.controller;

import site.mygumi.goodbite.security.EmailUserDetails;
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.menu.dto.CreateMenuRequestDto;
import site.mygumi.goodbite.domain.menu.dto.MenuResponseDto;
import site.mygumi.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import site.mygumi.goodbite.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/menus")
@RestController
public class MenuController {

    private final MenuService menuService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createMenu(
        @Valid @RequestPart CreateMenuRequestDto createMenuRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @RequestPart MultipartFile image) {

        menuService.createMenu(createMenuRequestDto, userDetails.getUser(), image);
        return ResponseUtil.createOk();
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<DataResponseDto<MenuResponseDto>> getMenu(@PathVariable Long menuId) {
        return ResponseUtil.findOk(menuService.getMenu(menuId));
    }

    @GetMapping
    public ResponseEntity<DataResponseDto<List<MenuResponseDto>>> getAllMenus() {
        return ResponseUtil.findOk(menuService.getAllMenus());
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{menuId}")
    public ResponseEntity<MessageResponseDto> updateMenu(
        @PathVariable Long menuId, @Valid @RequestPart UpdateMenuRequestDto updateMenuRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @RequestPart(required = false) MultipartFile image) {

        menuService.updateMenu(menuId, updateMenuRequestDto, userDetails.getUser(), image);
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