package site.mygumi.goodbite.domain.user.owner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


/**
 * 전화번호 수정 요청을 위한 DTO 클래스입니다.
 * <p>사업자 회원의 전화번호를 수정할 때 사용됩니다.</p>
 *
 * <b>유효성 조건:</b>
 * <ul>
 *   <li>전화번호 형식: 010-0000-0000과 같은 형식이어야 함</li>
 * </ul>
 */
@Getter
public class UpdateOwnerPhoneNumberRequestDto {

    @NotBlank(message = "휴대폰번호를 입력해 주세요.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다. ex) 010-0000-0000\n")
    private String newPhoneNumber;
}
