package site.mygumi.goodbite.domain.user.customer.controller;

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
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.user.customer.dto.CustomerResponseDto;
import site.mygumi.goodbite.domain.user.customer.dto.CustomerSignupRequestDto;
import site.mygumi.goodbite.domain.user.customer.dto.UpdateNicknameRequestDto;
import site.mygumi.goodbite.domain.user.customer.dto.UpdatePasswordRequestDto;
import site.mygumi.goodbite.domain.user.customer.dto.UpdatePhoneNumberRequestDto;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.service.CustomerService;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;

/**
 * 고객 관련 API를 제공하는 컨트롤러 클래스입니다.
 * <p>
 * 이 클래스는 회원가입, 회원 정보 수정, 조회, 탈퇴 등의 기능을 담당하며 {@link CustomerService}를 호출하여 실제 비즈니스 로직을 수행합니다.
 * </p>
 *
 * <b>주요 기능:</b>
 * <ul>
 *   <li>회원가입</li>
 *   <li>회원 정보 수정 (닉네임, 전화번호, 비밀번호)</li>
 *   <li>회원 정보 조회</li>
 *   <li>회원 탈퇴</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 회원가입 API
     * <p>
     * 클라이언트로부터 회원가입 요청을 받아 {@link CustomerService}를 통해 회원가입을 처리합니다.
     * </p>
     *
     * @param requestDto 회원가입 요청 내용을 담은 DTO
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(
        @Valid @RequestBody CustomerSignupRequestDto requestDto) {
        customerService.signup(requestDto);
        return ResponseUtil.createOk();
    }

    /**
     * 회원 정보 수정 (닉네임 변경) API
     * <p>
     * 클라이언트로부터 새로운 닉네임을 받아 {@link CustomerService}를 통해 닉네임을 업데이트합니다.
     * </p>
     *
     * @param requestDto  새로운 닉네임을 담은 DTO
     * @param userDetails 인증된 사용자 정보
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
     * 회원 정보 수정 (전화번호 변경) API
     * <p>
     * 클라이언트로부터 새로운 전화번호를 받아 {@link CustomerService}를 통해 전화번호를 업데이트합니다.
     * </p>
     *
     * @param requestDto  새로운 전화번호를 담은 DTO
     * @param userDetails 인증된 사용자 정보
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
     * 회원 정보 수정 (비밀번호 변경) API
     * <p>
     * 클라이언트로부터 새로운 비밀번호를 받아 {@link CustomerService}를 통해 비밀번호를 업데이트합니다.
     * </p>
     *
     * @param requestDto  새로운 비밀번호를 담은 DTO
     * @param userDetails 인증된 사용자 정보
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
     * 회원 정보 조회 API
     * <p>
     * 인증된 사용자의 정보를 조회하여 반환합니다.
     * </p>
     *
     * @param userDetails 인증된 사용자 정보
     * @return 회원 정보를 담은 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<CustomerResponseDto>> getCustomer(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        return ResponseUtil.findOk(customerService.getCustomer(userDetails.getUser()));
    }

    /**
     * 회원 탈퇴 API
     * <p>
     * 인증된 사용자의 계정을 탈퇴 처리합니다.
     * </p>
     *
     * @param userDetails 인증된 사용자 정보
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @DeleteMapping
    public ResponseEntity<MessageResponseDto> deleteCustomer(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        customerService.deleteCustomer((Customer) userDetails.getUser());
        return ResponseUtil.deleteOk();
    }

}
