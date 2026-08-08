package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.ChatRoomParticipant;
import kr.adapterz.jpa_practice.entity.MessageRole;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetParticitpantDto {
    // 채팅방 개별 인원 프로필정보
    // 닉네임, MessageRole
    private Long userId;
    private String nickname;
    private MessageRole messageRole;

    public GetParticitpantDto(ChatRoomParticipant chatRoomParticipant, boolean isHost) {
        this.userId = chatRoomParticipant.getUser().getUserId();
        this.nickname = chatRoomParticipant.getUser().getNickname();
        this.messageRole = isHost ? MessageRole.HOST : MessageRole.GENERAL;
    }
}
