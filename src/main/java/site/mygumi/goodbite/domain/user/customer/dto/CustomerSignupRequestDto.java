package site.mygumi.goodbite.domain.user.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 회원가입 요청 시 필요한 사용자 정보를 담는 DTO 클래스입니다.
 * <p>
 * 이메일, 닉네임, 비밀번호, 휴대폰 번호 필드를 포함하며, 각각의 필드에는 유효성 검사를 위한 제약 조건이 적용되어 있습니다.
 * </p>
 * <b>유효성 검사 규칙:</b>
 * <ul>
 *   <li>이메일: 이메일 형식을 준수</li>
 *   <li>닉네임: 최소 2자 이상 20자 이하, 숫자만으로 구성 불가</li>
 *   <li>비밀번호: 최소 8자 이상 15자 이하, 알파벳 대소문자, 숫자, 특수문자를 포함</li>
 *   <li>휴대폰 번호: "010-0000-0000" 형식 준수</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Getter
public class CustomerSignupRequestDto {

    /**
     * 사용자 이메일
     * <p>이메일 형식을 검증하며, 빈 문자열이 허용되지 않습니다.</p>
     */
    @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
        message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    /**
     * 사용자 닉네임
     * <p>닉네임은 2자 이상 20자 이하이며, 한글, 영어, 숫자 조합만 허용되며, 숫자만으로 구성될 수 없습니다.</p>
     */
    @NotBlank(message = "닉네임을 입력해 주세요")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^(?![0-9]+$)[a-zA-Z가-힣0-9]+$", message = "닉네임은 한글, 영어, 숫자를 포함할 수 있으며 숫자만으로는 구성될 수 없습니다.")
    private String nickname;

    /**
     * 사용자 비밀번호
     * <p>비밀번호는 8자 이상 15자 이하이며, 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다.</p>
     */
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자로 구성되어야 합니다.")
    private String password;

    /**
     * 사용자 휴대폰 번호
     * <p>휴대폰 번호는 "010-0000-0000" 형식을 준수해야 합니다.</p>
     */
    @NotBlank(message = "휴대폰번호를 입력해 주세요.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다. ex) 010-0000-0000\n")
    private String phoneNumber;
}