package site.mygumi.goodbite.domain.auth.dto;

public record KakaoUserResponseDto(String nickname, String email) {

    public static KakaoUserResponseDto from(String nickname, String email) {
        return new KakaoUserResponseDto(nickname, email);
    }
}