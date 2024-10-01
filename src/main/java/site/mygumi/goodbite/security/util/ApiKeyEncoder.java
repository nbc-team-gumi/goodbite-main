package site.mygumi.goodbite.security.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ApiKeyEncoder {

    public static String encodeApiKey(String apiKey) {//API 키를 인코딩하는 정적 메서드
        try {
            return URLEncoder.encode(apiKey,
                StandardCharsets.UTF_8.toString());//API 키를 UTF-8로 인코딩하고 반환
        } catch (Exception e) {
            throw new RuntimeException("API키를 인코딩하는데에 실패했습니다", e);
        }
    }
}
