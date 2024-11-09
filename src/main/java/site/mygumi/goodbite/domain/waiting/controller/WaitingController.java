package site.mygumi.goodbite.domain.waiting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;
import site.mygumi.goodbite.domain.waiting.dto.CreateWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.UpdateWaitingRequestDto;
import site.mygumi.goodbite.domain.waiting.dto.WaitingResponseDto;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.service.WaitingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waitings")
public class WaitingController {

    private final WaitingService waitingService;

    // 손님만 등록
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> createWaiting(
        @Valid @RequestBody CreateWaitingRequestDto createWaitingRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        return ResponseUtil.createOk(
            waitingService.createWaiting(createWaitingRequestDto, userDetails.getUser()));
    }

    // 손님이 보는 웨이팅 목록 조회
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    public ResponseEntity<DataResponseDto<Page<WaitingResponseDto>>> getMyWaitings(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PageableDefault(size = Waiting.DEFAULT_PAGE_SIZE) Pageable pageable) {

        return ResponseUtil.createOk(waitingService.getMyWaitings(userDetails.getUser(), pageable));
    }

    // 웨이팅 단일 조회
    @GetMapping("/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> getWaiting(
        @PathVariable Long waitingId, @AuthenticationPrincipal EmailUserDetails userDetails) {

        return ResponseUtil.findOk(waitingService.getWaiting(waitingId, userDetails.getUser()));
    }

    // 가게 주인이 웨이팅 ID 하나 선택 후 웨이팅 줄이기
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @PutMapping("/{waitingId}")
    public ResponseEntity<MessageResponseDto> decrementWaitingOrder(
        @PathVariable Long waitingId, @AuthenticationPrincipal EmailUserDetails userDetails) {

        waitingService.decrementWaitingOrder(waitingId, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    // 웨이팅 정보 수정
    @PatchMapping("/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> updateWaiting(
        @PathVariable Long waitingId,
        @Valid @RequestBody UpdateWaitingRequestDto updateWaitingRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {

        return ResponseUtil.updateOk(
            waitingService.updateWaiting(waitingId, updateWaitingRequestDto,
                userDetails.getUser()));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{waitingId}/entrance")
    public ResponseEntity<MessageResponseDto> enterWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.updateOk();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{waitingId}/no-show")
    public ResponseEntity<MessageResponseDto> noShowWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.updateOk();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{waitingId}/cancellation")
    public ResponseEntity<MessageResponseDto> cancelWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.updateOk();
    }


    // 웨이팅 취소
    @DeleteMapping("/{waitingId}")
    public ResponseEntity<MessageResponseDto> deleteWaiting(
        @PathVariable Long waitingId, @AuthenticationPrincipal EmailUserDetails userDetails) {

        waitingService.deleteWaiting(waitingId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }
}
