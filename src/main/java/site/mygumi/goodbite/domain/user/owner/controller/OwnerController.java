package site.mygumi.goodbite.domain.user.owner.controller;

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
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;
import site.mygumi.goodbite.domain.user.owner.dto.OwnerResponseDto;
import site.mygumi.goodbite.domain.user.owner.dto.OwnerSignUpRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateBusinessNumberRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerNicknameRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerPasswordRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.UpdateOwnerPhoneNumberRequestDto;
import site.mygumi.goodbite.domain.user.owner.service.BusinessVerificationService;
import site.mygumi.goodbite.domain.user.owner.service.OwnerService;

/**
 * 사업자 회원 관련 API를 제공하는 컨트롤러입니다.
 * <p>
 * 회원가입, 회원정보 조회 및 수정, 회원 탈퇴 등의 작업을 처리합니다.
 * </p>
 *
 * <b>주요 기능:</b>
 * <ul>
 *   <li>사업자 회원가입</li>
 *   <li>회원 정보 조회 및 수정 (닉네임, 전화번호, 사업자번호, 비밀번호)</li>
 *   <li>회원 탈퇴</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final BusinessVerificationService businessVerificationService;
    //private final BusinessVerificationServiceRest verificationService;

    /**
     * 회원가입 API
     * <p>요청된 회원가입 정보를 처리하여 신규 사업자를 등록합니다.</p>
     *
     * @param requestDto 회원가입 요청 데이터를 담은 DTO
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signUp(
        @Valid @RequestBody OwnerSignUpRequestDto requestDto) {
        ownerService.signup(requestDto);
        return ResponseUtil.createOk();
    }
    /*@PostMapping("/signup")
    public Mono<ResponseEntity<MessageResponseDto>> signUp(
        @Valid @RequestBody OwnerSignUpRequestDto requestDto) {

        return businessVerificationService.verifyBusinessNumber(requestDto.getBusinessNumber())
            .map(isBusinessNumberValid -> {
                if (!isBusinessNumberValid) {
                    return ResponseEntity.badRequest()
                        .body(new MessageResponseDto("Invalid business number"));
                }
                ownerService.signup(requestDto);
                return ResponseUtil.createOk();
            });
    }*/
    /*@PostMapping("/signup")
    public Mono<ResponseEntity<MessageResponseDto>> signUp(
        @Validated @RequestBody OwnerSignUpRequestDto requestDto) {
        return businessVerificationService.verifyBusinessNumber(requestDto.getBusinessNumber())
            .flatMap(isBusinessNumberValid -> {
                if (!isBusinessNumberValid) {
                    return Mono.just(ResponseEntity.badRequest()
                        .body(new MessageResponseDto("Invalid business number")));
                }
                return ownerService.signup(requestDto)
                    .then(Mono.just(ResponseUtil.createOk()));
            });
    }*/
    /*@PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signUp(
        @Validated @RequestBody OwnerSignUpRequestDto requestDto) {
        Boolean isBusinessNumberValid = verificationService.verifyBusinessNumber(
            requestDto.getBusinessNumber());
        if (!isBusinessNumberValid) {
            return ResponseEntity.badRequest()
                .body(new MessageResponseDto("Invalid business number"));
        }
        // 회원가입 로직
        return ResponseUtil.createOk();
    }*/


    /**
     * 회원정보 조회 API
     * <p>인증된 사용자의 회원 정보를 조회합니다.</p>
     *
     * @param userDetails 인증된 사용자 정보를 담고 있는 객체
     * @return 회원 정보를 담은 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<OwnerResponseDto>> getOwner(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        return ResponseUtil.findOk(
            ownerService.getOwner(userDetails.getUser()));
    }

    /**
     * 회원정보 수정 (닉네임) API
     * <p>요청된 새로운 닉네임으로 사용자 정보를 수정합니다.</p>
     *
     * @param requestDto  새로운 닉네임을 담은 DTO
     * @param userDetails 인증된 사용자 정보를 담고 있는 객체
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
     * 회원정보 수정 (전화번호) API
     * <p>요청된 새로운 전화번호로 사용자 정보를 수정합니다.</p>
     *
     * @param requestDto  새로운 전화번호를 담은 DTO
     * @param userDetails 인증된 사용자 정보를 담고 있는 객체
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
     * 회원정보 수정 (사업자번호) API
     * <p>요청된 새로운 사업자번호로 사용자 정보를 수정합니다.</p>
     *
     * @param requestDto  새로운 사업자번호를 담은 DTO
     * @param userDetails 인증된 사용자 정보를 담고 있는 객체
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
     * 회원정보 수정 (비밀번호) API
     * <p>요청된 새로운 비밀번호로 사용자 비밀번호를 수정합니다.</p>
     *
     * @param requestDto  새로운 비밀번호를 담은 DTO
     * @param userDetails 인증된 사용자 정보를 담고 있는 객체
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
     * 회원 탈퇴 API
     * <p>인증된 사용자의 계정을 탈퇴 처리합니다.</p>
     *
     * @param userDetails 인증된 사용자 정보를 담고 있는 객체
     * @return 성공 메시지를 담은 ResponseEntity
     */
    @DeleteMapping
    public ResponseEntity<MessageResponseDto> deleteOwner(
        @AuthenticationPrincipal EmailUserDetails userDetails) {
        ownerService.deleteOwner(userDetails.getUser());
        return ResponseUtil.deleteOk();
    }

}