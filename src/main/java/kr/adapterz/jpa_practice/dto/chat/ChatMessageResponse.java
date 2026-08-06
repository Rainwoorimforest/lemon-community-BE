package kr.adapterz.jpa_practice.dto.chat;

import kr.adapterz.jpa_practice.entity.Chat;
import kr.adapterz.jpa_practice.entity.ChatRole;
import kr.adapterz.jpa_practice.entity.MessageRole;
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

    private ChatRole chatRole;
    private MessageRole messageRole;

    private LocalDateTime createdAt;

    public ChatMessageResponse(Chat chat) {
        this.messageId = chat.getChatId();

        // SYSTEM 메시지(user가 null)일 때를 위한 안전한 분기 처리
        if (chat.getUser() != null) {
            this.senderId = chat.getUser().getUserId();
            this.senderNickname = chat.getUser().getNickname();
        } else {
            this.senderId = null;
            this.senderNickname = "시스템"; // 프론트에서 보여줄 시스템 이름
        }

        this.message = chat.getMessage();
        this.chatRole = chat.getChatRole();
        this.messageRole = chat.getMessageRole();
        this.createdAt = chat.getCreatedAt();
    }


}
