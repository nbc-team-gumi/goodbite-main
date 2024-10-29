package site.mygumi.goodbite.auth.dto;

/**
 * 로그인 성공 시 전달되는 응답 DTO 입니다.
 *
 * @param message 성공 메시지
 * @param role    사용자 역할: customer / owner
 * @author a-white-bit
 */
public record LoginSuccessResponseDto(String message, String role) {

    public static LoginSuccessResponseDto from(String message, String role) {
        return new LoginSuccessResponseDto(message, role);
    }
}
