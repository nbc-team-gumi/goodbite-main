package site.mygumi.goodbite.domain.owner.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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