package com.sparta.goodbite.domain.owner.controller;

import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.owner.dto.OwnerResponseDto;
import com.sparta.goodbite.domain.owner.dto.OwnerSignUpRequestDto;
import com.sparta.goodbite.domain.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    /**
     * 회원가입 API
     *
     * @param requestDto 회원가입 내용
     * @return 성공 메시지를 담은 ResponseEntity
     */

    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signUp(
        @Valid @RequestBody OwnerSignUpRequestDto requestDto) {
        ownerService.signUp(requestDto);
        return ResponseUtil.createOk();
    }

    /**
     * 회원정보조회 API
     *
     * @param ownerId 조회할 고객의 ID
     * @return ResponseEntity
     */

    @GetMapping("/{ownerId}")
    public ResponseEntity<DataResponseDto<OwnerResponseDto>> getOwner(
        @PathVariable Long ownerId/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ) {
        return ResponseUtil.findOk(ownerService.getOwner(ownerId));
    }
}
