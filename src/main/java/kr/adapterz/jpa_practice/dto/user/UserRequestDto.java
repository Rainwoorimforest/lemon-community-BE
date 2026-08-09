package kr.adapterz.jpa_practice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class UserRequestDto { // 회원가입 시 DTO

    @NotBlank(message = "이메일이 비어있습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;


    @NotBlank(message = "비밀번호가 비었습니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다."
    )
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;


    @NotBlank(message = "비밀번호 확인이 비었습니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다."
    )
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String passwordCheck;


    @NotBlank(message = "닉네임이 비었습니다.")
    @Pattern(regexp = "^\\S+$", message = "띄어쓰기(공백)를 포함할 수 없습니다.")
    @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
    private String nickname;


    private MultipartFile profileImage;
}
