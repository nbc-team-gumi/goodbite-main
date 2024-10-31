package site.mygumi.goodbite.domain.user.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 사용자 닉네임 변경 요청을 처리하기 위한 DTO 클래스입니다.
 * <p>
 * 새로운 닉네임을 담고 있으며, 해당 필드에 대해 유효성 검사가 적용됩니다.
 * </p>
 * <b>유효성 검사 규칙:</b>
 * <ul>
 *   <li>닉네임: 최소 2자 이상 20자 이하</li>
 *   <li>한글, 영어, 숫자 조합 허용 (숫자만으로 구성 불가)</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Getter
public class UpdateNicknameRequestDto {

    /**
     * 변경할 새로운 닉네임
     * <p>새로운 닉네임은 2자 이상 20자 이하이어야 하며, 한글, 영어, 숫자를 포함할 수 있으나 숫자만으로 구성될 수 없습니다.</p>
     */
    @NotBlank(message = "새 닉네임을 입력해 주세요")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^(?![0-9]+$)[a-zA-Z가-힣0-9]+$", message = "닉네임은 한글, 영어, 숫자를 포함할 수 있으며 숫자만으로는 구성될 수 없습니다.")
    private String newNickname;

}
