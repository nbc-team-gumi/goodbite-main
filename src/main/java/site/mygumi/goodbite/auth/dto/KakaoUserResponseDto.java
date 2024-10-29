package site.mygumi.goodbite.auth.dto;

/**
 * 카카오 로그인 서비스 요청에 대한 응답 DTO 입니다.
 *
 * @param nickname 사용자 카카오 닉네임
 * @param email    사용자 카카오 이메일
 * @author a-white-bit
 */
public record KakaoUserResponseDto(String nickname, String email) {

    public static KakaoUserResponseDto from(String nickname, String email) {
        return new KakaoUserResponseDto(nickname, email);
    }
}