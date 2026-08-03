package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageResponse {

    private Long messageId;
    private String message;
    private Long senderId;
    private String senderNickname;

    private LocalDateTime createdAt;

    public ChatMessageResponse(Chat chat) { // TODO: 엔티티 구현시 연결
        this.messageId = chat.getChatId();
        this.senderId = chat.getUser() != null ? chat.getUser().getUserId() : null; // SYSTEM 메시지는 user가 null이므로 분기 처리
        this.message = chat.getMessage();
        this.createdAt = chat.getCreatedAt();
    }

    // MessageController에서 웹소켓 용도로 쓰기 위한 생성자 추가
    public ChatMessageResponse(Long senderId, String senderNickname, String message) {
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }


}
