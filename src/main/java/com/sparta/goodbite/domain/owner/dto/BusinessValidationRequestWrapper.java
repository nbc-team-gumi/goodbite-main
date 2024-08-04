package com.sparta.goodbite.domain.owner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessValidationRequestWrapper {

    @JsonProperty("businesses")
    private List<BusinessValidationRequestDto> businesses;
}
//필요없는 객체. 해당 객체는 여러개의 사업자번호에 대한 요청을 보내게 될때 사용.
//하지만 우리 서비스는 한번에 하나의 사업자번호만을 조회하면 되기때문에 필요없다.


