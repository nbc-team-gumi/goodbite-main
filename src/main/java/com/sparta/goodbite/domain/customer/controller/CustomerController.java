package com.sparta.goodbite.domain.customer.controller;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.customer.dto.CustomerSignUpRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateNicknameRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdatePasswordRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateTelNoRequestDto;
import com.sparta.goodbite.domain.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    /**
     * 회원가입 API
     * @param requestDto 회원가입 내용
     * @return ResponseUtil
     */

    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signUp(@Valid @RequestBody CustomerSignUpRequestDto requestDto) {
        customerService.signUp(requestDto);
        return ResponseUtil.createOk();
    }

    /**
     * 회원정보수정(닉네임) API
     * @param customerId 업데이트할 고객의 ID
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */

    @PutMapping("/{customerId}/nickname")
    public ResponseEntity<MessageResponseDto> updateNickname(@PathVariable Long customerId,@Valid @RequestBody
    UpdateNicknameRequestDto requestDto/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ){
        customerService.updateNickname(customerId,requestDto);
        return ResponseUtil.updateOk();
    }

    /**
     * 회원정보수정(전화번호) API
     * @param customerId 업데이트할 고객의 ID
     * @param requestDto 새로운 닉네임을 담은 DTO
     * @return 업데이트 성공 메시지를 담은 ResponseEntity
     */

    @PutMapping("/{customerId}/telNo")
    public ResponseEntity<MessageResponseDto> updateTelNo(@PathVariable Long customerId,@Valid @RequestBody
    UpdateTelNoRequestDto requestDto/*,@AuthenticationPrincipal UserDetailsImpl userDetails*/
    ){
        customerService.updateTelNo(customerId,requestDto);
        return ResponseUtil.updateOk();
    }

}
