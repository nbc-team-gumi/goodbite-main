package site.mygumi.goodbite.domain.user.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateNicknameRequestDto {

    @NotBlank(message = "새 닉네임을 입력해 주세요")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^(?![0-9]+$)[a-zA-Z가-힣0-9]+$", message = "닉네임은 한글, 영어, 숫자를 포함할 수 있으며 숫자만으로는 구성될 수 없습니다.")
    private String newNickname;

}
