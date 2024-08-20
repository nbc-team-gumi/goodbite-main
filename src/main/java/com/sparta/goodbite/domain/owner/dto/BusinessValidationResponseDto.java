package com.sparta.goodbite.domain.owner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*
- API 응답이 중첩된 구조를 가지고 있어서 DTO 내에 static 클래스를 사용하여 이러한 중첩 구조를 표현하여 API 응답의 계층 구조를 명확히 나타냄
- 내부 클래스는 해당 DTO 내에서만 사용됨
- BusinessData클래스는 BusinessValidationResponseDto의 일부임을 나타내기 위해
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

