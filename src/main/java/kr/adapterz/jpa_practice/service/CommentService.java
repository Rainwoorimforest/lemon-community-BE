package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.comment.*;
import kr.adapterz.jpa_practice.entity.Comment;
import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.User;
import kr.adapterz.jpa_practice.exception.NotFoundException;
import kr.adapterz.jpa_practice.exception.AccessDeniedException;
import kr.adapterz.jpa_practice.repository.CommentRepository;
import kr.adapterz.jpa_practice.repository.PostRepository;
import kr.adapterz.jpa_practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.adapterz.jpa_practice.security.CustomUserDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentCreateResponseDto createComment(Long postId, CommentRequestDto request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        User author = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Comment comment = new Comment(
                request.getCommentContent(),
                author,
                post
        );

        comment.setPost(post); // 연관관계 매핑
        post.getPostInfo().increaseCommentCount();
        Comment savedComment = commentRepository.save(comment);
        return new CommentCreateResponseDto(savedComment);
    }

    public CommentListResponseDto getAllComment(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        List<Comment> comments = post.getComments();

        List<CommentResponseDto> dtoList = comments.stream()
                .peek(comment -> comment.checkAndUpdateNickname())
                .map(comment -> new CommentResponseDto(comment))
                .toList();

        return new CommentListResponseDto(dtoList, postId);
    }

    @Transactional
    public CommentUpdateResponseDto updateComment(Long postId, Long commentId, CustomUserDetails userDetails, CommentRequestDto request) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        if (!comment.getAuthor().getUserId().equals(userDetails.getUserId())) {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        if(request.getCommentContent() != null)
        {
            comment.checkAndUpdateNickname();
            comment.changeContent(request.getCommentContent());
        }

        return new CommentUpdateResponseDto(comment);
    }

    @Transactional
    public CommentDeleteResponseDto deleteComment(Long postId, Long commentId, CustomUserDetails userDetails) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        // 본인이 생성한 댓글만 삭제할 수 있음
        if (!comment.getAuthor().getUserId().equals(userDetails.getUserId()))
        {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        comment.disconnectPost(post);
        post.getPostInfo().decreaseCommentCount();
        commentRepository.delete(comment);

        return new CommentDeleteResponseDto(comment);
    }
}
