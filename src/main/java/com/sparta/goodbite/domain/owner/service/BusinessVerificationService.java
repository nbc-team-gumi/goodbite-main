package com.sparta.goodbite.domain.owner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class BusinessVerificationService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public BusinessVerificationService(RestTemplate restTemplate,
        @Value("${publicdata.api.key}") String apiKey,
        @Value("${publicdata.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public boolean verifyBusinessNumber(String businessNumber) {
        String url = String.format("%s?serviceKey=%s&businessNumber=%s", apiUrl, apiKey,
            businessNumber);
        System.out.println("Request URL: " + url);  // 디버깅용 로그
        try {
            BusinessVerificationResponse response = restTemplate.getForObject(url,
                BusinessVerificationResponse.class);
            return response != null && "valid".equals(response.getStatus());
        } catch (HttpClientErrorException e) {
            // HttpClientErrorException 처리
            // 예를 들어, 잘못된 요청이나 권한 오류 등
            e.printStackTrace();
            return false;
        } catch (RestClientException e) {
            // RestClientException 처리
            // 네트워크 오류나 다른 예외 등
            e.printStackTrace();
            return false;
        }
    }

    // 공공데이터포털 API의 응답 형식에 맞는 클래스 작성
    public static class BusinessVerificationResponse {

        private String status;

        // getters and setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


}
