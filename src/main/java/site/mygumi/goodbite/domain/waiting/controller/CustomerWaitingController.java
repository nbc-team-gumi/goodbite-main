package site.mygumi.goodbite.domain.waiting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/customers/waitings")
@RestController
public class CustomerWaitingController {

    private final WaitingService waitingService;

    @PostMapping
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> createWaiting(
        @Valid @RequestBody CreateWaitingRequestDto createWaitingRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.createOk(
            waitingService.createWaiting(createWaitingRequestDto, userDetails.getUser()));
    }

    @GetMapping("/me")
    public ResponseEntity<DataResponseDto<Page<WaitingResponseDto>>> getMyWaitings(
        @AuthenticationPrincipal EmailUserDetails userDetails,
        @PageableDefault(size = Waiting.DEFAULT_PAGE_SIZE) Pageable pageable
    ) {
        return ResponseUtil.createOk(waitingService.getMyWaitings(userDetails.getUser(), pageable));
    }

    @GetMapping("/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> getWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.findOk(
            waitingService.customerGetWaiting(waitingId, userDetails.getUser())
        );
    }

    @PatchMapping("/{waitingId}")
    public ResponseEntity<DataResponseDto<WaitingResponseDto>> updateWaiting(
        @PathVariable Long waitingId,
        @Valid @RequestBody UpdateWaitingRequestDto updateWaitingRequestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.updateOk(
            waitingService.updateWaiting(
                waitingId,
                updateWaitingRequestDto,
                userDetails.getUser()
            )
        );
    }

    @PatchMapping("/{waitingId}/cancellation")
    public ResponseEntity<MessageResponseDto> cancelWaiting(
        @PathVariable Long waitingId,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        waitingService.cancelWaiting(waitingId, userDetails.getUser());
        return ResponseUtil.updateOk();
    }
}
