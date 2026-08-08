package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ChatRoomParticipant {

    @EmbeddedId
    @Column(nullable = false)
    private ChatRoomParticipantId chatRoomParticipantId;

    // 누가 참여했는가?
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // 어느 방에 참여했는가?
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId")
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;
    
    // 언제 처음 들어왔는가?
    private LocalDateTime joinedAt;
    
    protected ChatRoomParticipant() {}
    
    public ChatRoomParticipant(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.joinedAt = LocalDateTime.now();
        this.chatRoomParticipantId = new ChatRoomParticipantId(chatRoom.getRoomId(), user.getUserId());
    }
}
