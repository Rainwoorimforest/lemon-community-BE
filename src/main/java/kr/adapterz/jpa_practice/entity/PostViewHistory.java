package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

// TODO: 조회기록 테이블 erd cloud에 작성하기
@Table(name = "post_view_histories")
@Entity
@Getter @Setter
public class PostViewHistory {

    @EmbeddedId
    @Column(nullable = false)
    private PostViewHistoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "last_viewed_at", nullable = false)
    private LocalDateTime lastViewedAt;

    protected PostViewHistory() {}

    public PostViewHistory(User user, Post post) {
        this.user = user;
        this.post = post;
        this.id = new PostViewHistoryId();
        this.lastViewedAt = LocalDateTime.now();
    }

    // 마지막 조회 시간 갱신 메서드
    public void updateLastViewedAt() {
        this.lastViewedAt = LocalDateTime.now();
    }
}