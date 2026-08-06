package kr.adapterz.jpa_practice.dto.post;

import kr.adapterz.jpa_practice.dto.comment.CommentResponseDto;
import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.PostInfo;
import kr.adapterz.jpa_practice.entity.PostImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;

    private String title;
    private String content;
    private List<String> images;
    private Long authorId;
    private String nickname;
    private int likeCount; // 좋아요수
    private int commentCount; // 댓글수
    private int viewCount; // 조회수
    private Long roomId;

    private List<CommentResponseDto> comments;

    private LocalDateTime createdAt;


    public PostResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.authorId = post.getAuthor().getUserId();
        this.nickname = post.getAuthor().getNickname();
        this.content = post.getContent();

        this.images = post.getPostImages().stream()
                .map(PostImage::getContentImage)
                .collect(Collectors.toList());

        this.likeCount = (post.getPostInfo() != null) ? post.getPostInfo().getLikeCount() : 0;
        this.commentCount = (post.getPostInfo() != null) ? post.getPostInfo().getCommentCount() : 0;
        this.viewCount = (post.getPostInfo() != null) ? post.getPostInfo().getViewCount() : 0;

        this.createdAt = post.getCreatedAt();

        this.comments = post.getComments().stream()
                .map(comment -> new CommentResponseDto(comment))
                .collect(Collectors.toList());

        // roomId 반환
        if (post.getChatRoom() != null && !post.getChatRoom().isEmpty()) {
            this.roomId = post.getChatRoom().getLast().getRoomId();
        } else {
            this.roomId = null;
        }

    }
}
