package site.mygumi.goodbite.domain.user.owner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 닉네임 수정 요청을 위한 DTO 클래스입니다.
 * <p>사용자의 닉네임을 수정할 때 필요한 데이터와 유효성 조건을 정의합니다.</p>
 *
 * <b>유효성 조건:</b>
 * <ul>
 *   <li>새 닉네임은 2자 이상 20자 이하의 길이를 가져야 합니다.</li>
 *   <li>한글, 영어, 숫자를 포함할 수 있으며, 숫자만으로 구성될 수 없습니다.</li>
 * </ul>
 */
@Getter
public class UpdateOwnerNicknameRequestDto {

    @NotBlank(message = "새 닉네임을 입력해 주세요")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^(?![0-9]+$)[a-zA-Z가-힣0-9]+$", message = "닉네임은 한글, 영어, 숫자를 포함할 수 있으며 숫자만으로는 구성될 수 없습니다.")
    private String newNickname;
}
