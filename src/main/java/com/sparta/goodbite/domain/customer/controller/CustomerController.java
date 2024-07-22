package com.sparta.goodbite.domain.customer.controller;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.domain.customer.dto.CustomerSignUpRequestDto;
import com.sparta.goodbite.domain.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
