package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Embeddable
@Getter @Setter
@EqualsAndHashCode
public class PostViewHistoryId implements Serializable {
    private Long postId;
    private Long userId;

    public PostViewHistoryId() {}

    public PostViewHistoryId(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}