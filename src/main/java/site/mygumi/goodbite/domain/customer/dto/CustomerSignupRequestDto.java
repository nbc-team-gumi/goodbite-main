package site.mygumi.goodbite.domain.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CustomerSignupRequestDto {

    @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
        message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임을 입력해 주세요")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^(?![0-9]+$)[a-zA-Z가-힣0-9]+$", message = "닉네임은 한글, 영어, 숫자를 포함할 수 있으며 숫자만으로는 구성될 수 없습니다.")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자로 구성되어야 합니다.")
    private String password;

    @NotBlank(message = "휴대폰번호를 입력해 주세요.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다. ex) 010-0000-0000\n")
    private String phoneNumber;
}