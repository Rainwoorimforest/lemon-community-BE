package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder // 빌더를 이용해서 DTO를 재사용
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private Long roomId;
    private String title;
    private String summary;
    private String notice;
    private int participantCount;
    private Long hostId;
    private List<ChatMessageResponse> historyChats;

    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.title = chatRoom.getRoomTitle();
        this.summary = chatRoom.getRoomSummary();
        this.participantCount = chatRoom.getParticipantCount();
        this.hostId = chatRoom.getHost().getUserId();
    }

    public ChatRoomResponseDto(ChatRoom chatRoom, List<ChatMessageResponse> historyChats) {
        this.roomId = chatRoom.getRoomId();
        this.title = chatRoom.getRoomTitle();
        this.summary = chatRoom.getRoomSummary();
        this.notice = chatRoom.getRoomNotice();
        this.participantCount = chatRoom.getParticipantCount();
        this.hostId = chatRoom.getHost().getUserId();

        this.historyChats = historyChats;
    }


}

