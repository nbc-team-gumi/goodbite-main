package site.mygumi.goodbite.domain.operatinghour.controller;

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
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.dto.OperatingHourResponseDto;
import site.mygumi.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.service.OperatingHourService;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;

/**
 * 영업시간 관련 요청을 처리하는 컨트롤러 클래스입니다. 영업시간 생성, 조회, 수정, 삭제와 관련된 HTTP 요청을 처리합니다.
 *
 * @author haeuni00
 */
@RestController
@RequestMapping("/operating-hours")
@RequiredArgsConstructor
public class OperatingHourController {

    private final OperatingHourService operatingHourService;

    /**
     * 영업시간을 생성합니다.
     *
     * @param createOperatingHourRequestDto 영업시간 생성 요청 정보
     * @param userDetails                   현재 인증된 사용자 정보
     * @return 영업시간 생성 성공 여부를 포함한 응답
     */
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createOperatingHour(@Valid @RequestBody
    CreateOperatingHourRequestDto createOperatingHourRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.createOperatingHour(createOperatingHourRequestDto,
            userDetails.getUser());
        return ResponseUtil.createOk();
    }

    /**
     * 영업시간을 수정합니다.
     *
     * @param operatingHourId               수정할 영업시간의 ID
     * @param updateOperatingHourRequestDto 영업시간 수정 요청 정보
     * @param userDetails                   현재 인증된 사용자 정보
     * @return 영업시간 수정 성공 여부를 포함한 응답
     */
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

    /**
     * 영업시간 ID로 해당 영업시간을 조회합니다.
     *
     * @param operatingHourId 조회할 영업시간의 ID
     * @return 해당 영업시간 정보를 포함한 DTO
     */
    @GetMapping("/{operatingHourId}")
    public ResponseEntity<DataResponseDto<OperatingHourResponseDto>> getOperatingHour(
        @PathVariable Long operatingHourId) {

        return ResponseUtil.findOk(operatingHourService.getOperatingHour(operatingHourId));
    }

    /**
     * 영업시간을 삭제합니다.
     *
     * @param operatingHourId 삭제할 영업시간의 ID
     * @param userDetails     현재 인증된 사용자 정보
     * @return 영업시간 삭제 성공 여부를 포함한 응답
     */
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{operatingHourId}")
    public ResponseEntity<MessageResponseDto> deleteOperatingHour(
        @PathVariable Long operatingHourId,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        operatingHourService.deleteOperatingHour(operatingHourId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}