package kr.adapterz.jpa_practice.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageResponse {

    private String message;
    private Long senderId;
    private String senderNickname;

    private LocalDateTime createdAt;

//    public ChatMessageResponse(Chat chat) { // TODO: 엔티티 구현시 연결
//        this.sender = chat.getSender();
//        this.message = chat.getMessage();
//        this.createdAt = chat.getCreatedAt();
//    }

    public ChatMessageResponse(Long senderId, String senderNickname, String message ) {
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.message = message;
    }
}
