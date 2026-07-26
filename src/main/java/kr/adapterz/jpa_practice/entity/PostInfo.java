package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class PostInfo {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    protected PostInfo() {}

    public PostInfo(Post post) {
        this.post = post;

        this.commentCount = post.getComments().size();
        this.likeCount = post.getLikes().size();

        post.linkPostInfo(this);
    }


    public void increaseCommentCount() {
        commentCount++;
    }

    public void decreaseCommentCount() {
        if(this.commentCount > 0) {
            commentCount--;
        }
    }

    public void increaseLikeCount() {
        likeCount ++;
    }

    public void decreaseLikeCount() {
        if(this.likeCount > 0) {
            likeCount --;
        }
    }
}
