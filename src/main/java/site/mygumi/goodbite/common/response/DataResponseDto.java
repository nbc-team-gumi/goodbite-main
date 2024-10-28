package site.mygumi.goodbite.common.response;

/**
 * 상태 코드, 메시지, 응답 데이터를 포함하는 데이터 전송 객체입니다.
 * <p>
 * 이 클래스는 HTTP 응답의 상태 코드와 메시지, 그리고 본문 데이터를 하나의 객체로 묶어 반환할 때 사용됩니다. 제네릭 타입 {@code <T>}을 사용하여 다양한 데이터
 * 타입을 유연하게 담을 수 있습니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * DataResponseDto<String> response = new DataResponseDto<>(200, "성공", "데이터 본문");
 * </pre>
 * </p>
 *
 * @param <T>        응답 데이터 타입
 * @param statusCode HTTP 상태 코드
 * @param message    응답 메시지
 * @param data       응답 데이터
 * @author a-white-bit
 */
public record DataResponseDto<T>(int statusCode, String message, T data) {

}