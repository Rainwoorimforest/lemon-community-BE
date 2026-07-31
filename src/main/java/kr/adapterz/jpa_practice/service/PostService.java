package kr.adapterz.jpa_practice.service;

import jakarta.validation.constraints.Positive;
import kr.adapterz.jpa_practice.dto.post.*;
import kr.adapterz.jpa_practice.dto.user.UserResponseDto;
import kr.adapterz.jpa_practice.entity.*;
import kr.adapterz.jpa_practice.exception.*;
import kr.adapterz.jpa_practice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostViewHistoryRepository postViewHistoryRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto request) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                author
        );

        PostInfo postInfo = new PostInfo(post);

        // 게시글에 이미지를 첨부하였을 경우
        if(!request.getImages().isEmpty()) {
            for (String url: request.getImages())
            {
                if(url != null) post.addPostImage(url);
            }
        }

        Post savedPost = postRepository.save(post);

        return new PostResponseDto(savedPost);
    }

    @Transactional
    public PostResponseDto getPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        PostViewHistoryId historyId = new PostViewHistoryId(postId, userId);
        Optional<PostViewHistory> historyOpt = postViewHistoryRepository.findById(historyId);

        boolean shouldIncrease = false;

        if (historyOpt.isEmpty()) { //
            shouldIncrease = true;

            PostViewHistory newHistory = new PostViewHistory(user, post);
            postViewHistoryRepository.save(newHistory);
        }
        else {
            PostViewHistory history = historyOpt.get(); //

            // 조회수 로직 - 3시간이 지났는지 체크
            if(history.getLastViewedAt().isBefore(LocalDateTime.now().minusHours(3)))
            {
                shouldIncrease = true;
                history.updateLastViewedAt();
            }

        }

        if (shouldIncrease)
        {
            postViewHistoryRepository.flush(); // 메모리가 clear 되기 전에 조회기록 반영

            postRepository.increaseViewCount(postId);
            // post.getPostInfo().increaseViewCount(); // 불필요한 코드. 넣으면 중복으로 올라간다. 위의 코드가 PostInfo를 UPDATE

            // 중요: increaseViewCount(Atomic Update)를 호출하면
            // clearAutomatically = true 에 의해 영속성 컨텍스트가 비워집니다.
            // 수정된 최신 DB 상태(업데이트된 조회수)를 DTO에 반영하기 위해 다시 조회해옵니다.
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));
        }

        post.checkAndUpdateNickname();

        return new PostResponseDto(post);
    }

    @Transactional
    public List<AllPostsResponseDto> getAllPost(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        List<Post> posts = postPage.getContent();

        return posts.stream()
                .peek(post -> {
                    post.checkAndUpdateNickname();
                })
                .map(post -> new AllPostsResponseDto(post))
                .collect(Collectors.toList());
    }

    @Transactional
    public PostUpdateResponseDto updatePost(Long postId, CustomUserDetails userDetails, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        // 작성자 검증
        if (!userDetails.getUserId().equals(post.getAuthor().getUserId())) {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        // 제목, 내용 업데이트할때 null 값 확인하는 코드 추가
        if (request.getTitle() != null) post.changeTitle(request.getTitle());
        if (request.getContent() != null) post.changeContent(request.getContent());

        return new PostUpdateResponseDto(post);
    }

    @Transactional
    public void deletePost(Long postId, CustomUserDetails userDetails) {

        // 먼저 Post 자체가 있는지 확인하는 구문 추가
        Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        // 현재 삭제를 시도한 유저가 작성자인지 체크
        if (!userDetails.getUserId().equals(post.getAuthor().getUserId())) {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        // 연관된 댓글과 좋아요도 지우기
        if (post.getComments() != null)
        {
            for(Comment comment: post.getComments())
            {
                commentRepository.delete(comment);
            }
        }

        if(post.getLikes() != null)
        {
            for(Like like : post.getLikes())
            {
                likeRepository.delete(like);
            }
        }

        postRepository.delete(post);
    }
}
