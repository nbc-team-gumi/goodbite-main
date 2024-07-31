package com.sparta.goodbite.domain.customer.controller;

import com.sparta.goodbite.auth.security.EmailUserDetails;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.customer.dto.CustomerResponseDto;
import com.sparta.goodbite.domain.customer.dto.CustomerSignupRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateNicknameRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePasswordRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePhoneNumberRequestDto;
import com.sparta.goodbite.domain.customer.service.CustomerService;
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
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 회원가입 API
     *
     * @param requestDto 회원가입 내용
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(
        @Valid @RequestBody CustomerSignupRequestDto requestDto) {
        customerService.signup(requestDto);
        return ResponseUtil.createOk();
    }

    /**
     * 회원정보수정(닉네임) API
     *
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponseDto> updateNickname(
        @Valid @RequestBody UpdateNicknameRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        customerService.updateNickname(requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(전화번호) API
     *
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */
    @PatchMapping("/phone-number")
    public ResponseEntity<MessageResponseDto> updatePhoneNumber(
        @Valid @RequestBody UpdatePhoneNumberRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        customerService.updatePhoneNumber(requestDto, userDetails.getUser());
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
        @Valid @RequestBody UpdatePasswordRequestDto requestDto,
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        customerService.updatePassword(requestDto, userDetails.getUser());
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보조회 API
     *
     * @return ResponseEntity
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<CustomerResponseDto>> getCustomer(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        return ResponseUtil.findOk(customerService.getCustomer(userDetails.getUser()));
    }

    /**
     * 회원탈퇴 API
     *
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @DeleteMapping
    public ResponseEntity<MessageResponseDto> deleteCustomer(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        customerService.deleteCustomer(userDetails.getUser());
        return ResponseUtil.deleteOk();
    }

}
