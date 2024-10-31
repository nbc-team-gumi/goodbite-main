package site.mygumi.goodbite.domain.user.owner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 사업자 번호 유효성 검증 API 응답을 처리하기 위한 DTO 클래스입니다.
 * <p>
 * API 응답 구조가 중첩된 형태이므로 내부에 `BusinessData` 클래스를 정의하여 중첩된 응답 데이터를 처리하고 계층 구조를 명확히 표현합니다.
 * </p>
 *
 * <b>주요 필드:</b>
 * <ul>
 *   <li>statusCode: 응답 상태 코드</li>
 *   <li>matchCount: 일치하는 사업자 번호의 개수</li>
 *   <li>requestCount: 요청된 사업자 번호의 개수</li>
 *   <li>data: 사업자 정보 데이터를 담고 있는 리스트</li>
 * </ul>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Getter
@Setter
public class BusinessValidationResponseDto {

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("match_cnt")
    private int matchCount;

    @JsonProperty("request_cnt")
    private int requestCount;

    @JsonProperty("data")
    private List<BusinessData> data;

    @Getter
    @Setter
    public static class BusinessData {

        @JsonProperty("b_no")
        private String businessNumber;

        @JsonProperty("b_stt")
        private String businessStatus;

        @JsonProperty("b_stt_cd")
        private String businessStatusCode;

        @JsonProperty("tax_type")
        private String taxType;

        @JsonProperty("tax_type_cd")
        private String taxTypeCode;

        @JsonProperty("end_dt")
        private String endDate;

        @JsonProperty("utcc_yn")
        private String utccYn;

        @JsonProperty("tax_type_change_dt")
        private String taxTypeChangeDate;

        @JsonProperty("invoice_apply_dt")
        private String invoiceApplyDate;

        @JsonProperty("rbf_tax_type")
        private String rbfTaxType;

        @JsonProperty("rbf_tax_type_cd")
        private String rbfTaxTypeCode;
    }
}

