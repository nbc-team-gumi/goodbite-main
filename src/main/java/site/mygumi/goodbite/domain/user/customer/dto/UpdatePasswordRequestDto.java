package site.mygumi.goodbite.domain.user.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 사용자 비밀번호 변경 요청을 처리하기 위한 DTO 클래스입니다.
 * <p>
 * 현재 비밀번호와 새로운 비밀번호를 포함하고 있으며, 새로운 비밀번호에는 유효성 검사가 적용됩니다.
 * </p>
 * <b>유효성 검사 규칙:</b>
 * <ul>
 *   <li>새 비밀번호: 최소 8자 이상 15자 이하</li>
 *   <li>알파벳 대소문자, 숫자, 특수문자를 포함해야 함</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Getter
public class UpdatePasswordRequestDto {

    /**
     * 현재 비밀번호
     * <p>기존 비밀번호를 검증하기 위해 사용됩니다.</p>
     */
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    /**
     * 변경할 새로운 비밀번호
     * <p>새로운 비밀번호는 8자 이상 15자 이하이며, 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다.</p>
     */
    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자로 구성되어야 합니다.")
    private String newPassword;
}