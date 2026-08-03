package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; // TODO: Setter는 직접적으로 사용하지말고 불러올땐 메소드에 의도가 보이도록 합시다.

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter @Setter
@SequenceGenerator(
        name = "comment_seq",
        sequenceName = "comment_seq",
        allocationSize = 5
)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private String commentContent;

    @Column(nullable = false, length = 10)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // DB FK 연결
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Comment() {}

    public Comment(String commentContent, User author, Post post) {
        this.author = author;
        this.nickname = author.getNickname();
        this.post = post;
        this.commentContent = commentContent;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setPost(Post post) {
        this.post = post;

        if(post !=null) {
            post.getComments().add(this);
        }
    }

    public void disconnectPost(Post post) {

        if(post != null) {
            post.getComments().remove(this);
            this.post = null;
        }

    }

    public void changeContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public void checkAndUpdateNickname() {
        if(!this.nickname.equals(this.author.getNickname()))
        {
            this.nickname = this.author.getNickname();
        }
    }

}
