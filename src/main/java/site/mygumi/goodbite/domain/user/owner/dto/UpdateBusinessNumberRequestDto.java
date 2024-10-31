package site.mygumi.goodbite.domain.user.owner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * 사업자 번호 수정 요청을 위한 DTO 클래스입니다.
 * <p>사용자의 사업자 번호를 수정할 때 필요한 데이터와 유효성 조건을 정의합니다.</p>
 *
 * <b>유효성 조건:</b>
 * <ul>
 *   <li>새 사업자 번호는 10자리 숫자 형식이어야 합니다.</li>
 * </ul>
 */
@Getter
public class UpdateBusinessNumberRequestDto {

    @Pattern(regexp = "\\d{10}", message = "사업자 번호는 10자리 숫자여야 합니다.")
    @NotBlank(message = "사업자번호를 입력해주세요")
    private String newBusinessNumber;
}
