package com.sparta.goodbite.domain.operatinghour.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.service.OperatingHourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operating-hours")
@RequiredArgsConstructor
public class OperatingHourController {

    private final OperatingHourService operatingHourService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createOperatingHour(@Valid @RequestBody
    CreateOperatingHourRequestDto createOperatingHourRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.createOperatingHour(createOperatingHourRequestDto, userDetails);
        return ResponseUtil.createOk();
    }

    @PutMapping("/{operatingHourId}")
    public ResponseEntity<MessageResponseDto> updateOperatingHour(
        @PathVariable Long operatingHourId,
        @Valid @RequestBody UpdateOperatingHourRequestDto updateOperatingHourRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.updateOperatingHour(operatingHourId, updateOperatingHourRequestDto,
            userDetails);
        return ResponseUtil.updateOk();
    }

    @DeleteMapping("/{operatingHourId}")
    public ResponseEntity<MessageResponseDto> deleteOperatingHour(
        @PathVariable Long operatingHourId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.deleteOperatingHour(operatingHourId, userDetails);
        return ResponseUtil.deleteOk();
    }
}