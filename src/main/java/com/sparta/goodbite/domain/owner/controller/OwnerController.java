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
     * @return ResponseEntity
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<OwnerResponseDto>> getOwner(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        return ResponseUtil.findOk(
            ownerService.getOwner(userDetails.getUser()));
    }

    /**
     * 회원정보수정(닉네임) API
     *
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponseDto> updateNickname(
        @Valid @RequestBody UpdateOwnerNicknameRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        ownerService.updateNickname(requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(전화번호) API
     *
     * @param requestDto 새로운 전화번호를 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/phone-number")
    public ResponseEntity<MessageResponseDto> updatePhoneNumber(
        @Valid @RequestBody UpdateOwnerPhoneNumberRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        ownerService.updatePhoneNumber(requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(사업자번호) API
     *
     * @param requestDto 새로운 사업자번호을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/business-number")
    public ResponseEntity<MessageResponseDto> updateBusinessNumber(
        @Valid @RequestBody UpdateBusinessNumberRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        ownerService.updateBusinessNumber(requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(비밀번호) API
     *
     * @param requestDto 새로운 비밀번호를 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/password")
    public ResponseEntity<MessageResponseDto> updatePassword(
        @Valid @RequestBody UpdateOwnerPasswordRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        ownerService.updatePassword(requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원탈퇴 API
     *
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @DeleteMapping
    public ResponseEntity<MessageResponseDto> deleteOwner(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        ownerService.deleteOwner(userDetails.getUser());
        return ResponseUtil.deleteOk();
    }

}