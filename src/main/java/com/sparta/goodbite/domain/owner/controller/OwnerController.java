package com.sparta.goodbite.domain.owner.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.owner.dto.OwnerResponseDto;
import com.sparta.goodbite.domain.owner.dto.OwnerSignUpRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateBusinessNumberRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateOwnerNicknameRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateOwnerPasswordRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateOwnerPhoneNumberRequestDto;
import com.sparta.goodbite.domain.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        ownerService.signup(requestDto);
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
        @PathVariable Long ownerId, @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        return ResponseUtil.findOk(
            ownerService.getOwner(ownerId, userDetails.getUser()));
    }

    /**
     * 회원정보수정(닉네임) API
     *
     * @param ownerId    업데이트할 사장의 ID
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/{ownerId}/nickname")
    public ResponseEntity<MessageResponseDto> updateNickname(
        @PathVariable Long ownerId,
        @Valid @RequestBody
        UpdateOwnerNicknameRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        ownerService.updateNickname(ownerId, requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(전화번호) API
     *
     * @param ownerId    업데이트할 사장의 ID
     * @param requestDto 새로운 전화번호를 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/{ownerId}/phone-number")
    public ResponseEntity<MessageResponseDto> updatePhoneNumber(@PathVariable Long ownerId,
        @Valid @RequestBody UpdateOwnerPhoneNumberRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        ownerService.updatePhoneNumber(ownerId, requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(사업자번호) API
     *
     * @param ownerId    업데이트할 사장의 ID
     * @param requestDto 새로운 사업자번호을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/{ownerId}/business-number")
    public ResponseEntity<MessageResponseDto> updateBusinessNumber(@PathVariable Long ownerId,
        @Valid @RequestBody
        UpdateBusinessNumberRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        ownerService.updateBusinessNumber(ownerId, requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(비밀번호) API
     *
     * @param ownerId    업데이트할 사장의 ID
     * @param requestDto 새로운 비밀번호를 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/{ownerId}/password")
    public ResponseEntity<MessageResponseDto> updatePassword(@PathVariable Long ownerId,
        @Valid @RequestBody
        UpdateOwnerPasswordRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        ownerService.updatePassword(ownerId, requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원탈퇴 API
     *
     * @param ownerId 탈퇴할 사장의 ID
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<MessageResponseDto> deleteOwner(
        @PathVariable Long ownerId, @AuthenticationPrincipal EmailUserDetails userDetails
    ) {
        ownerService.deleteOwner(ownerId, userDetails.getUser());
        return ResponseUtil.deleteOk();
    }

}