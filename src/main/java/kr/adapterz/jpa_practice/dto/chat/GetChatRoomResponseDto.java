package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetChatRoomResponseDto {
    private Long roomId;
    private String title;
    private String summary;
    private String notice;
    private int participantCount;
    private List<ChatMessageResponse> historyChats;

    public GetChatRoomResponseDto(ChatRoom chatRoom, List<ChatMessageResponse> historyChats) {
        this.roomId = chatRoom.getRoomId();
        this.title = chatRoom.getRoomTitle();
        this.summary = chatRoom.getRoomSummary();
        this.notice = chatRoom.getRoomNotice();
        this.participantCount = chatRoom.getParticipantCount();

        this.historyChats = historyChats;
    }
}
