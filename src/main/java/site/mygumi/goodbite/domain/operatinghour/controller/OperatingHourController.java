package site.mygumi.goodbite.domain.operatinghour.controller;

import site.mygumi.goodbite.security.EmailUserDetails;
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import site.mygumi.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.service.OperatingHourService;
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

@RestController
@RequestMapping("/operating-hours")
@RequiredArgsConstructor
public class OperatingHourController {

    private final OperatingHourService operatingHourService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createOperatingHour(@Valid @RequestBody
    CreateOperatingHourRequestDto createOperatingHourRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.createOperatingHour(createOperatingHourRequestDto,
            userDetails.getUser());
        return ResponseUtil.createOk();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{operatingHourId}")
    public ResponseEntity<MessageResponseDto> updateOperatingHour(
        @PathVariable Long operatingHourId,
        @Valid @RequestBody UpdateOperatingHourRequestDto updateOperatingHourRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.updateOperatingHour(operatingHourId, updateOperatingHourRequestDto,
            userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    @GetMapping("/{operatingHourId}")
    public ResponseEntity<DataResponseDto<OperatingHourResponseDto>> getOperatingHour(
        @PathVariable Long operatingHourId) {

        return ResponseUtil.findOk(operatingHourService.getOperatingHour(operatingHourId));
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{operatingHourId}")
    public ResponseEntity<MessageResponseDto> deleteOperatingHour(
        @PathVariable Long operatingHourId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.deleteOperatingHour(operatingHourId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}