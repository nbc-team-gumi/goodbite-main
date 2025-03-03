package site.mygumi.goodbite.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * HTTP 응답 생성을 위한 유틸리티 클래스입니다.
 * <p>
 * 이 클래스는 CRUD 작업에 대한 표준 응답을 생성하거나, {@link HttpServletResponse}를 통해 JSON 응답을 반환하는 다양한 정적 메서드를
 * 제공합니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * ResponseEntity<MessageResponseDto> response = ResponseUtil.createOk();
 * ResponseUtil.servletApi(response, HttpStatus.OK.value(), "메시지");
 * </pre>
 * </p>
 *
 * @author a-white-bit
 */
public abstract class ResponseUtil {

    /**
     * 메시지를 포함한 {@link ResponseEntity} 객체를 생성합니다.
     *
     * @param httpStatus HTTP 상태 코드
     * @param message    응답 메시지
     * @return {@link MessageResponseDto}를 포함한 ResponseEntity
     */
    public static ResponseEntity<MessageResponseDto> of(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
            .body(new MessageResponseDto(httpStatus.value(), message));
    }

    /**
     * 메시지와 데이터를 포함한 {@link ResponseEntity} 객체를 생성합니다.
     *
     * @param httpStatus HTTP 상태 코드
     * @param message    응답 메시지
     * @param data       응답 데이터
     * @return {@link DataResponseDto}를 포함한 ResponseEntity
     */
    public static <T> ResponseEntity<DataResponseDto<T>> of(HttpStatus httpStatus, String message,
        T data) {
        return ResponseEntity.status(httpStatus)
            .body(new DataResponseDto<>(httpStatus.value(), message, data));
    }

    /**
     * 생성 성공 메시지와 데이터를 포함한 {@link ResponseEntity} 객체를 반환합니다.
     *
     * @param data 응답 데이터
     * @return {@link DataResponseDto}를 포함한 ResponseEntity
     */
    public static <T> ResponseEntity<DataResponseDto<T>> createOk(T data) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponseDto<>(HttpStatus.OK.value(), "생성 성공", data));
    }

    /**
     * 조회 성공 메시지와 데이터를 포함한 {@link ResponseEntity} 객체를 반환합니다.
     *
     * @param data 응답 데이터
     * @return {@link DataResponseDto}를 포함한 ResponseEntity
     */
    public static <T> ResponseEntity<DataResponseDto<T>> findOk(T data) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponseDto<>(HttpStatus.OK.value(), "조회 성공", data));
    }

    /**
     * 수정 성공 메시지와 데이터를 포함한 {@link ResponseEntity} 객체를 반환합니다.
     *
     * @param data 응답 데이터
     * @return {@link DataResponseDto}를 포함한 ResponseEntity
     */
    public static <T> ResponseEntity<DataResponseDto<T>> updateOk(T data) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponseDto<>(HttpStatus.OK.value(), "수정 성공", data));
    }

    /**
     * 삭제 성공 메시지를 포함한 {@link ResponseEntity} 객체를 반환합니다.
     *
     * @return {@link MessageResponseDto}를 포함한 ResponseEntity
     */
    public static ResponseEntity<MessageResponseDto> deleteOk() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponseDto(HttpStatus.OK.value(), "삭제 성공"));
    }

    /**
     * 생성 성공 메시지를 포함한 {@link ResponseEntity} 객체를 반환합니다.
     *
     * @return {@link MessageResponseDto}를 포함한 ResponseEntity
     */
    public static ResponseEntity<MessageResponseDto> createOk() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponseDto(HttpStatus.OK.value(), "생성 성공"));
    }

    /**
     * 수정 성공 메시지를 포함한 {@link ResponseEntity} 객체를 반환합니다.
     *
     * @return {@link MessageResponseDto}를 포함한 ResponseEntity
     */
    public static ResponseEntity<MessageResponseDto> updateOk() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponseDto(HttpStatus.OK.value(), "수정 성공"));
    }

    /**
     * 서블릿 컨테이너를 거치지 않고 사용자에게 응답 메시지를 반환
     *
     * @param response   서블릿 응답 객체
     * @param httpStatus 응답 http 상태
     * @param message    응답 메시지
     * @throws IOException JSON <-> 객체 매핑 예외처리
     */
    public static void servletApi(HttpServletResponse response, int httpStatus,
        String message) throws IOException {
        // 응답 상태 설정
        response.setStatus(httpStatus);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        // 응답 메시지 설정
        String result = new ObjectMapper().writeValueAsString(
            new MessageResponseDto(httpStatus, message));

        // 응답 데이터 반환
        response.getWriter().write(result);
    }

    /**
     * 서블릿 컨테이너를 거치지 않고 사용자에게 응답 메시지와 데이터를 반환합니다.
     *
     * @param response   서블릿 응답 객체
     * @param httpStatus 응답 http 상태
     * @param data       응답 데이터
     * @throws IOException JSON <-> 객체 매핑 예외처리
     */
    public static void servletApi(HttpServletResponse response, int httpStatus, Object data)
        throws IOException {
        // 응답 상태 설정
        response.setStatus(httpStatus);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        // 응답 데이터 설정
        String result = new ObjectMapper().writeValueAsString(data);

        // 응답 데이터 반환
        response.getWriter().write(result);
    }
}