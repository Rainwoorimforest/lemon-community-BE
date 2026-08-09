package kr.adapterz.jpa_practice.dto.comment;

import kr.adapterz.jpa_practice.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto { // 댓글 전체 조회
    private Long commentId;
    private Long postId; // 이거 commentInfo로 한꺼번에 주려고햇는데..
    private Long userId;
    private String author;
    private String authorProfileImg;
    private String content;
    private LocalDateTime createdAt;

    // private final Long postId;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.userId = comment.getAuthor().getUserId();
        this.author = comment.getNickname(); // 반정규화 부분. service 계층에서 동기화 해줘야 한다.
        this.authorProfileImg = comment.getAuthor().getProfileImage();
        this.content = comment.getCommentContent();
        this.createdAt = comment.getCreatedAt();
    }
}