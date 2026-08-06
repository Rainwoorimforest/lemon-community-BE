package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteChatRoomResponseDto {
    private Long roomId;
    private Long postId;

    public DeleteChatRoomResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.postId = chatRoom.getPost().getPostId();
    }
}
