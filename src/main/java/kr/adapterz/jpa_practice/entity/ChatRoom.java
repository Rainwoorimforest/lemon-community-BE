package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SequenceGenerator(
        name = "chat_room_seq",
        sequenceName = "chat_room_seq",
        allocationSize = 5
)
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_room_seq")
    @Column(name = "rood_id", nullable = false)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "room_title")
    private String roomTitle;

    @Column(name = "room_summary")
    private String roomSummary;

    @Column(name = "room_notice")
    private String roomNotice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User host;

    @Column(name = "participant_count", nullable = false)
    private int participantCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    protected ChatRoom() {}

    public ChatRoom(User user, Post post, String roomTitle, String roomSummary) {
        this.post = post;
        this.host = user;
        this.roomTitle = roomTitle;
        this.roomSummary = roomSummary;
        this.participantCount = 0;

        this.createdAt = LocalDateTime.now();
    }

    // 채팅방 공지 생성 및 수정
    public void assignNotice(String notice) {
        this.roomNotice = notice;
    }

    // 채팅방 제목 수정
    public void updateRoomTitle(String title) {
        this.roomTitle = title;
    }

    // 채팅방 소제목 수정
    public void updateRoomSummary(String roomSummary) {
        this.roomSummary = roomSummary;
    }

    // 채팅방 인원수
    public void increaseParticipant() {
        participantCount ++;
    }

    public void decreaseParticipant() {participantCount --;}

    public int getParticipant() {
        return this.participantCount;
    }


}

