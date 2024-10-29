package site.mygumi.goodbite.domain.user.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * 사용자 전화번호 변경 요청을 처리하기 위한 DTO 클래스입니다.
 * <p>
 * 새로운 전화번호를 포함하고 있으며, 전화번호는 지정된 형식에 맞아야 합니다.
 * </p>
 * <b>유효성 검사 규칙:</b>
 * <ul>
 *   <li>전화번호: "010-0000-0000" 형식을 준수해야 함</li>
 * </ul>
 */
@Getter
public class UpdatePhoneNumberRequestDto {

    /**
     * 변경할 새로운 전화번호
     * <p>전화번호는 "010-0000-0000" 형식이어야 합니다.</p>
     */
    @NotBlank(message = "휴대폰번호를 입력해 주세요.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다. ex) 010-0000-0000\n")
    private String newPhoneNumber;
}
