package kr.adapterz.jpa_practice.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileImageResponseDto {
    // private Long userId;
    private String profileImgUrl;

    public ProfileImageResponseDto(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
