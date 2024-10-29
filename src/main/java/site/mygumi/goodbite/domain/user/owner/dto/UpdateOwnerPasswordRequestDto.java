package site.mygumi.goodbite.domain.user.owner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 비밀번호 수정 요청을 위한 DTO 클래스입니다.
 * <p>사업자 회원의 비밀번호를 수정할 때 사용됩니다.</p>
 *
 * <b>유효성 조건:</b>
 * <ul>
 *   <li>새 비밀번호: 8~15자 길이, 알파벳 대소문자, 숫자, 특수문자를 포함해야 함</li>
 * </ul>
 */
@Getter
public class UpdateOwnerPasswordRequestDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자로 구성되어야 합니다.")
    private String newPassword;
}
