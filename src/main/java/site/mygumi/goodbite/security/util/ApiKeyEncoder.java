package site.mygumi.goodbite.security.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * API 키를 UTF-8 형식으로 URL 인코딩하는 유틸리티 클래스입니다.
 * <p>
 * 이 클래스는 API 키를 인코딩하여 URL 내에서 안전하게 전달할 수 있도록 변환하는 메서드를 제공합니다.
 * </p>
 *
 * @author a-whit-bit
 */
public class ApiKeyEncoder {

    /**
     * 주어진 API 키를 UTF-8 형식으로 인코딩합니다.
     * <p>
     * 이 메서드는 API 키를 UTF-8 형식으로 URL 인코딩하여 반환하며, 인코딩 중 예외 발생 시 런타임 예외를 던집니다.
     * </p>
     *
     * @param apiKey 인코딩할 API 키
     * @return UTF-8 형식으로 인코딩된 API 키
     * @throws RuntimeException 인코딩 중 예외 발생 시 던져지는 예외
     */
    public static String encodeApiKey(String apiKey) {//API 키를 인코딩하는 정적 메서드
        try {
            return URLEncoder.encode(apiKey,
                StandardCharsets.UTF_8.toString());//API 키를 UTF-8로 인코딩하고 반환
        } catch (Exception e) {
            throw new RuntimeException("API키를 인코딩하는데에 실패했습니다", e);
        }
    }
}
