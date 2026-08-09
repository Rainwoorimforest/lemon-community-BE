package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.ChatRoomParticipant;
import kr.adapterz.jpa_practice.entity.MessageRole;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetParticitpantDto {

    private Long userId;
    private String nickname;
    private String profileImg;
    private MessageRole messageRole;

    public GetParticitpantDto(ChatRoomParticipant chatRoomParticipant, boolean isHost) {
        this.userId = chatRoomParticipant.getUser().getUserId();
        this.nickname = chatRoomParticipant.getUser().getNickname();
        this.profileImg = chatRoomParticipant.getUser().getProfileImage();
        this.messageRole = isHost ? MessageRole.HOST : MessageRole.GENERAL;
    }
}
