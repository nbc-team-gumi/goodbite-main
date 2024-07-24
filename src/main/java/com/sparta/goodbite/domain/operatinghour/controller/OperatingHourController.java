package com.sparta.goodbite.domain.operatinghour.controller;

import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import com.sparta.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.service.OperatingHourService;
import jakarta.validation.Valid;
import java.util.List;
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

@RestController
@RequestMapping("/operating-hours")
@RequiredArgsConstructor
public class OperatingHourController {

    private final OperatingHourService operatingHourService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createOperatingHour(@Valid @RequestBody
    CreateOperatingHourRequestDto createOperatingHourRequestDto) {

        operatingHourService.createOperatingHour(createOperatingHourRequestDto);
        return ResponseUtil.createOk();
    }

    @PutMapping("/{operatingHourId}")
    public ResponseEntity<MessageResponseDto> updateOperatingHour(
        @PathVariable Long operatingHourId,
        @Valid @RequestBody UpdateOperatingHourRequestDto updateOperatingHourRequestDto) {

        operatingHourService.updateOperatingHour(operatingHourId, updateOperatingHourRequestDto);
        return ResponseUtil.updateOk();
    }

    @DeleteMapping("/{operatingHourId}")
    public ResponseEntity<MessageResponseDto> deleteOperatingHour(
        @PathVariable Long operatingHourId) {

        operatingHourService.deleteOperatingHour(operatingHourId);
        return ResponseUtil.deleteOk();
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<DataResponseDto<List<OperatingHourResponseDto>>> getAllOperatingHoursByRestaurant(
        @PathVariable Long restaurantId) {

        return ResponseUtil.findOk(
            operatingHourService.getAllOperatingHoursByRestaurant(restaurantId));
    }
}