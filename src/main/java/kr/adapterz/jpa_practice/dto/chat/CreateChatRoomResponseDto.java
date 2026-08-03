package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.Chat;
import kr.adapterz.jpa_practice.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@Builder // 빌더를 이용해서 DTO를 재사용
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRoomResponseDto {
    private Long roomId;
    private String title;
    private String summary;
    private String notice; // 다른 POST 응답에 재사용하기 위함. null일수도
    private int participant; // 다른 POST 응답에 재사용하기 위함. null일수도

    public CreateChatRoomResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.title = chatRoom.getRoomTitle();
        this.summary = chatRoom.getRoomSummary();
        this.participant = chatRoom.getParticipantCount();
    }

}
