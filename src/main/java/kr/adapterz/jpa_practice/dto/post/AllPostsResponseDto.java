package kr.adapterz.jpa_practice.dto.post;

import kr.adapterz.jpa_practice.dto.comment.CommentResponseDto;
import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.PostImage;
import kr.adapterz.jpa_practice.entity.PostInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AllPostsResponseDto {


    private Long postId;
    private String title;
    private Long authorId;
    private String nickname;
    private String authorProfileImg;

    private int likeCount; // 좋아요수
    private int commentCount; // 댓글수
    private int viewCount; // 조회수

    private LocalDateTime createdAt;

    public AllPostsResponseDto(Post post) { // 이거 List컬렉션으로 가지고와야지
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.authorId = post.getAuthor().getUserId();
        this.nickname = post.getAuthor().getNickname();
        this.authorProfileImg = post.getAuthor().getProfileImage();

        this.likeCount = (post.getPostInfo() != null) ? post.getPostInfo().getLikeCount() : 0;
        this.commentCount = (post.getPostInfo() != null) ? post.getPostInfo().getCommentCount() : 0;
        this.viewCount = (post.getPostInfo() != null) ? post.getPostInfo().getViewCount() : 0;

        this.createdAt = post.getCreatedAt();

    }


}
