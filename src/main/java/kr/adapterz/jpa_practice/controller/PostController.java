package kr.adapterz.jpa_practice.controller;

import jakarta.validation.Valid;
import kr.adapterz.jpa_practice.dto.post.PostRequestDto;
import kr.adapterz.jpa_practice.dto.post.PostResponseDto;
import kr.adapterz.jpa_practice.dto.post.AllPostsResponseDto;
import kr.adapterz.jpa_practice.dto.post.PostUpdateResponseDto;
import kr.adapterz.jpa_practice.dto.post.PostDeleteRequestDto;
import kr.adapterz.jpa_practice.response.ApiResponse;
import kr.adapterz.jpa_practice.service.PostService;
import kr.adapterz.jpa_practice.service.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kr.adapterz.jpa_practice.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<AllPostsResponseDto>>> getAllPost (Pageable pageable) {
        List<AllPostsResponseDto> result = postService.getAllPost(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POSTS_RETRIEVED",result, null));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost (
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostResponseDto result = postService.getPost(postId, userDetails.getUserId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Location", "/users" + result.getPostId())
                .body(ApiResponse.of("POST_RETRIEVED",result, null));
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostRequestDto request
    ) {
        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("USER_MISMATCH");
        }
        PostResponseDto result = postService.createPost(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATED", result));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponseDto>> updatePost (
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostRequestDto request
    ) {
        PostUpdateResponseDto result = postService.updatePost(postId, userDetails, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_UPDATED", result));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostDeleteRequestDto request
    ) {
        postService.deletePost(postId, userDetails);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_DELETED", null));
    }
}
