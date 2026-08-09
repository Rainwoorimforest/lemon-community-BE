package kr.adapterz.jpa_practice.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {

    @NotBlank(message = "닉네임이 비었습니다.")
    @Pattern(regexp = "^\\S+$", message = "띄어쓰기(공백)를 포함할 수 없습니다.")
    @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
    private String nickname;

    private MultipartFile profileImage;
}
