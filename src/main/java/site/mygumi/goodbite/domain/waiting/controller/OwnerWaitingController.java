package site.mygumi.goodbite.domain.waiting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;
import site.mygumi.goodbite.domain.waiting.dto.WaitingResponseDto;
import site.mygumi.goodbite.domain.waiting.service.WaitingService;

@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
@RequestMapping("/owners/waitings")
@RestController
public class OwnerWaitingController {

    private final WaitingService waitingService;

    @GetMapping("/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> getWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.findOk(
            waitingService.ownerGetWaiting(waitingId, userDetails.getUser()));
    }

    @PatchMapping("/{waitingId}/entrance")
    public ResponseEntity<MessageResponseDto> enterWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        waitingService.enterWaiting(waitingId, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    @PatchMapping("/{waitingId}/no-show")
    public ResponseEntity<MessageResponseDto> noShowWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        waitingService.noShowWaiting(waitingId, userDetails.getUser());
        return ResponseUtil.updateOk();
    }
}
