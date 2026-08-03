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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String message;

    private LocalDateTime createdAt;

    // 1. JPA를 위한 기본 생성자는 protected로 막아둠 (무분별한 new 방지)
    protected Chat() {}

    // 2. 내부에서만 사용하는 private 생성자 (팩토리 메서드에서 호출됨)
    private Chat(User user, ChatRoom chatRoom, ChatRole chatRole, String message) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.chatRole = chatRole;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // ==========================================
    // 정적 팩토리 메서드 (Static Factory Methods)
    // ==========================================

    /**
     * 일반 유저가 채팅을 쳤을 때 사용하는 메서드
     */
    public static Chat createTalkMessage(User user, ChatRoom chatRoom, String message) {
        // TALK 역할로 세팅해서 반환
        return new Chat(user, chatRoom, ChatRole.TALK, message);
    }

    /**
     * 누군가 들어오거나 나갈 때 시스템이 띄우는 안내 메시지 생성 메서드
     */
    public static Chat createSystemMessage(ChatRoom chatRoom, String message) {
        // 시스템 메시지는 작성한 유저가 없으므로 user 자리에 null을 넣음
        return new Chat(null, chatRoom, ChatRole.SYSTEM, message);
    }
}
