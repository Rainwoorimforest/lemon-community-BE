package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@SequenceGenerator(
        name = "chat_seq",
        sequenceName = "chat_seq",
        allocationSize = 30
)
public class Chat {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq")
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    // 시스템 메시지의 경우 특정 유저가 없으므로 null을 허용해야 합니다. (nullable = false 제거)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_role", nullable = false)
    private ChatRole chatRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_role", nullable = false)
    private MessageRole messageRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String message;

    private LocalDateTime createdAt;

    // 1. JPA를 위한 기본 생성자는 protected로 막아둠 (무분별한 new 방지)
    protected Chat() {}

    // 2. 내부에서만 사용하는 private 생성자 (팩토리 메서드에서 호출됨)
    private Chat(User user, ChatRoom chatRoom, ChatRole chatRole, String message)
    {
        this.user = user;
        this.chatRoom = chatRoom;
        this.chatRole = chatRole;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.messageRole = assignMessageRole(user);

    }

    public static Chat createTalkMessage(User user, ChatRoom chatRoom, String message)
    {
        return new Chat(user, chatRoom, ChatRole.TALK, message);
    }

    public static Chat createSystemMessage(ChatRoom chatRoom, String message)
    {
        return new Chat(null, chatRoom, ChatRole.SYSTEM, message);
    }

    public MessageRole assignMessageRole(User user) //TODO: 확인
    {
        if(user != null && user.getUserId().equals(this.getChatRoom().getHost().getUserId())) // TODO: 방장인지 아닌지 확인하는 메서드
        {
            return MessageRole.HOST;
        }

        return MessageRole.GENERAL;
    }


}
