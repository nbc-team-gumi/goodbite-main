package site.mygumi.goodbite.domain.user.owner.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사업자 번호 유효성 검증 요청을 위한 DTO 클래스입니다.
 * <p>하나 이상의 사업자 번호를 담아 외부 API에 전달하는 데 사용됩니다.</p>
 *
 * <b>주요 필드:</b>
 * <ul>
 *   <li>b_no: 사업자 번호 목록</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessValidationRequestDto {

    private List<String> b_no;

    /*
    @JsonProperty("b_no")
    private String businessNumber;
    -> 요청 형식이 그냥 문자열이 아니라 문자열 배열이라 위의 방식으로 수정
    */
}