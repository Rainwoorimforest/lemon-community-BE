package kr.adapterz.jpa_practice.dto.user;

import kr.adapterz.jpa_practice.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import kr.adapterz.jpa_practice.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

// JS 프론트 과제를 위해서 만든 dto
@Getter
@NoArgsConstructor
public class UserAllResponseDto { // 회원가입 시 DTO
    private Long userId;
    private String email;
    private String nickname;
    private String profileImage;


    public UserAllResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
