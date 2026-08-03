package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.like.*;
import kr.adapterz.jpa_practice.entity.*;
import kr.adapterz.jpa_practice.exception.NotFoundException;
import kr.adapterz.jpa_practice.exception.AccessDeniedException;
import kr.adapterz.jpa_practice.repository.LikeRepository;
import kr.adapterz.jpa_practice.repository.PostRepository;
import kr.adapterz.jpa_practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.adapterz.jpa_practice.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public GetLikeInfoResponseDto getLikeInfo(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        return new GetLikeInfoResponseDto(post);
    }

    public LikeResponseDto pressLike(Long postId, Long userId, CustomUserDetails userDetails, LikeRequestDto request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("AUTH_DENIED");
        }

        if (request.isLike()) {
            LikeId likeId = new LikeId(postId, userId);

            likeRepository.findById(likeId).ifPresent(like -> {
                throw new NotFoundException("LIKE_ALREADY_PRESSED");
            });
        }

        Like like = new Like(true, user, post);

        like.setPost(post);
        post.getPostInfo().increaseLikeCount();
        likeRepository.save(like);

        return new LikeResponseDto(true, post);
    }

    public LikeResponseDto cancelLike(Long postId, Long userId, CustomUserDetails userDetails) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("AUTH_DENIED");
        }

        LikeId likeId = new LikeId(postId, userId);

        Like like = likeRepository.findById(likeId)
                        .orElseThrow(() -> new NotFoundException("LIKE_NEVER_PRESSED"));

        like.disconnectPost(post);
        post.getPostInfo().decreaseLikeCount();
        likeRepository.delete(like);

        return new LikeResponseDto(false, post);
    }
}