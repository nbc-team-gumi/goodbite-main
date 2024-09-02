package site.mygumi.goodbite.domain.auth.dto;

public record LoginSuccessResponseDto(String message, String role) {

    public static LoginSuccessResponseDto from(String message, String role) {
        return new LoginSuccessResponseDto(message, role);
    }
}
