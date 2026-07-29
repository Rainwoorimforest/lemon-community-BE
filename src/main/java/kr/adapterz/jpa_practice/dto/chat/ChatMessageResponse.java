package kr.adapterz.jpa_practice.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageResponse {

    private String sender;
    private String message;
    private LocalDateTime createdAt;

//    public ChatMessageResponse(Chat chat) {
//        this.sender = chat.getSender();
//        this.message = chat.getMessage();
//        this.createdAt = chat.getCreatedAt();
//    }

    public ChatMessageResponse(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
