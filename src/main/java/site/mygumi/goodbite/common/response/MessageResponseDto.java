package site.mygumi.goodbite.common.response;

/**
 * 상태 코드와 메시지를 포함하는 응답 데이터 전송 객체입니다.
 * <p>
 * 이 클래스는 HTTP 응답의 상태 코드와 메시지를 함께 묶어 반환할 때 사용됩니다. 주로 성공 또는 오류 메시지를 전달하는 간단한 응답 형식으로 사용됩니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * MessageResponseDto response = new MessageResponseDto(200, "성공");
 * </pre>
 * </p>
 *
 * @param statusCode HTTP 상태 코드
 * @param message    응답 메시지
 * @author a-white-bit
 */
public record MessageResponseDto(int statusCode, String message) {

}