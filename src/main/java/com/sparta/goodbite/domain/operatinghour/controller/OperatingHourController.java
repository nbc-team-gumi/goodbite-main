package com.sparta.goodbite.domain.operatinghour.controller;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.operatinghour.dto.OperatingHourRequestDto;
import com.sparta.goodbite.domain.operatinghour.service.OperatingHourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operationHours")
@RequiredArgsConstructor
public class OperatingHourController {

    private final OperatingHourService operatingHourService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createOperatingHour(@RequestBody
    OperatingHourRequestDto operatingHourRequestDto) {

        operatingHourService.createOperatingHour(operatingHourRequestDto);
        return ResponseUtil.createOk();
    }
}