package site.mygumi.goodbite.domain.user.owner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import site.mygumi.goodbite.domain.auth.util.ApiKeyEncoder;
import site.mygumi.goodbite.domain.user.owner.dto.BusinessValidationRequestDto;
import site.mygumi.goodbite.domain.user.owner.dto.BusinessValidationResponseDto;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class BusinessVerificationService {

    // 클래스 내부에서 사용할 의존성을 정의
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    // 생성자 통해 의존성 주입
    public BusinessVerificationService(RestTemplate restTemplate,
        ObjectMapper objectMapper,
        @Value("${publicdata.api.key}") String apiKey,
        @Value("${publicdata.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    // 사업자 번호 검증 메서드 정의
    public boolean verifyBusinessNumber(String businessNumber) {
        try {
            // ApiKeyEncoder.encodeApiKey를 이용해 API 키 인코딩
            String encodedApiKey = ApiKeyEncoder.encodeApiKey(apiKey);

            // API요청 URL 생성
            String url = String.format("%s?serviceKey=%s", apiUrl, encodedApiKey);

            // URL을 URI 객체로 변환
            URI uri = new URI(url);

            // HTTP 헤더를 생성
            HttpHeaders headers = new HttpHeaders();
            // 콘텐츠 타입을 JSON으로 설정
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 디버깅 위해 요청 URL 출력
            log.info("Request URL: {}", url);

            // 요청 DTO 객체를 생성
            BusinessValidationRequestDto requestDto = new BusinessValidationRequestDto(
                Collections.singletonList(businessNumber)
                // businessNumber라는 단일 요소를 포함하는 리스트를 반환. 리스트는 불변.
                // Arrays.asList도 동일한 결과를 만들수 있지만 단일요소를 다루고 있기때문에 Collections.singletonList를 사용했다.
            );

            // 요청 DTO 객체를 JSON 문자열로 변환
            String jsonRequest = objectMapper.writeValueAsString(
                requestDto); //writeValueAsString이 자바 객체를 JSON문자열로 변환해준다.
            // HTTP 엔티티를 생성
            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            // 디버깅 위해 요청 엔티티의 본문을 로그로 출력
            log.info("Request Entity: {}", entity.getBody());

            // API요청을 보내고 응답을 문자열 형태로 받음
            ResponseEntity<String> response = restTemplate.exchange(uri,
                HttpMethod.POST, entity, String.class);

            // 응답상태와 본문을 로그로 출력
            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());

            // 응답이 성공적이고 본문이 존재하는 경우
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 응답 본문을 DTO 객체로 변환
                BusinessValidationResponseDto responseDto = objectMapper.readValue(
                    response.getBody(), BusinessValidationResponseDto.class);

                // 파싱된 응답 본문을 로그로 출력
                log.info("파싱된 응답 본문 Parsed Response Body: {}", responseDto);

                // 응답 데이터가 존재하는 경우
                if (responseDto.getData() != null && !responseDto.getData().isEmpty()) {
                    // 필요한 필드를 사용
                    String businessNo = responseDto.getData().get(0).getBusinessNumber();
                    String businessStatusCode = responseDto.getData().get(0)
                        .getBusinessStatusCode();

                    // 필요한 필드를 로그로 출력
                    log.info("Business Number: {}", businessNo);
                    log.info("Business Status Code: {}", businessStatusCode);

                    // 사업자 상태 코드가 01인지 확인
                    return "01".equals(businessStatusCode);
                } else {
                    throw new RuntimeException("Empty data from API");
                }
            } else {
                throw new RuntimeException("Invalid response from API");
            }

        } catch (URISyntaxException e) {
            // URI 구문 오류를 로그로 출력하고 false 반환
            log.error("URI Syntax Exception: {}", e.getMessage(), e);
            return false;
        } catch (RestClientException e) {
            // REST 클라이언트 예외를 로그로 출력하고 false 반환
            log.error("Rest Client Exception: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            // 기타 예외를 로그로 출력하고 예외를 던짐
            log.error("Exception: {}", e.getMessage(), e);
            throw new RuntimeException("JSON processing failed", e);
        }
    }
}
