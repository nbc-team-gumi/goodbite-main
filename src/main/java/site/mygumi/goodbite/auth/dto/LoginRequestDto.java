package site.mygumi.goodbite.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 사용자 로그인 요청 DTO 입니다.
 *
 * @author a-white-bit
 */
@Getter
public class LoginRequestDto {

    @Email(message = "유효한 이메일 형식을 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @JsonProperty("isOwner")
    private boolean isOwner;

//    @Pattern(regexp = "^(CUSTOMER|OWNER|ADMIN)$", message = "역할은 CUSTOMER, OWNER, ADMIN 중 하나여야 합니다.")
//    @NotBlank(message = "역할은 필수 입력 항목입니다.")
//    private String role;
}