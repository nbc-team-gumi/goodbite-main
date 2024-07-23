package com.sparta.goodbite.domain.customer.controller;

import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.customer.dto.CustomerResponseDto;
import com.sparta.goodbite.domain.customer.dto.CustomerSignUpRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateNicknameRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePasswordRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePhoneNumberRequestDto;
import com.sparta.goodbite.domain.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @return ResponseUtil
     */

    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signUp(
        @Valid @RequestBody CustomerSignUpRequestDto requestDto) {
        customerService.signUp(requestDto);
        return ResponseUtil.createOk();
    }

    /**
     * 회원정보수정(닉네임) API
     *
     * @param customerId 업데이트할 고객의 ID
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */

    @PatchMapping("/{customerId}/nickname")
    public ResponseEntity<MessageResponseDto> updateNickname(@PathVariable Long customerId,
        @Valid @RequestBody
        UpdateNicknameRequestDto requestDto/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ) {
        customerService.updateNickname(customerId, requestDto);
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(전화번호) API
     *
     * @param customerId 업데이트할 고객의 ID
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */

    @PatchMapping("/{customerId}/phone-number")
    public ResponseEntity<MessageResponseDto> updatePhoneNumber(@PathVariable Long customerId,
        @Valid @RequestBody
        UpdatePhoneNumberRequestDto requestDto/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ) {
        customerService.updatePhoneNumber(customerId, requestDto);
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(비밀번호) API
     *
     * @param customerId 업데이트할 고객의 ID
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */

    @PatchMapping("/{customerId}/password")
    public ResponseEntity<MessageResponseDto> updatePassword(@PathVariable Long customerId,
        @Valid @RequestBody
        UpdatePasswordRequestDto requestDto/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ) {
        customerService.updatePassword(customerId, requestDto);
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보조회 API
     *
     * @param customerId 조회할 고객의 ID
     * @return ResponseEntity
     */

    @GetMapping("/{customerId}")
    public ResponseEntity<DataResponseDto<CustomerResponseDto>> getCustomer(
        @PathVariable Long customerId/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ) {
        return ResponseUtil.findOk(customerService.getCustomer(customerId));
    }

    /**
     * 회원탈퇴 API
     *
     * @param customerId 탈퇴할 고객의 ID
     * @return 성공 메시지를 담은 ResponseEntity
     */

    @DeleteMapping("/{customerId}")
    public ResponseEntity<MessageResponseDto> deleteCustomer(
        @PathVariable Long customerId/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ) {
        customerService.deleteCustomer(customerId);
        return ResponseUtil.deleteOk();
    }

}
