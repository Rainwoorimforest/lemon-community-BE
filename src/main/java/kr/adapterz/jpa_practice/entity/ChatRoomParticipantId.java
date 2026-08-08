package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@EqualsAndHashCode
public class ChatRoomParticipantId implements Serializable {
    private Long roomId;
    private Long userId;

    public ChatRoomParticipantId() {}

    public ChatRoomParticipantId(Long roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
    }
}
