package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.dto.like.GetLikeInfoResponseDto;
import kr.adapterz.jpa_practice.dto.like.LikeResponseDto;
import kr.adapterz.jpa_practice.dto.like.LikeRequestDto;
import kr.adapterz.jpa_practice.response.ApiResponse;
import kr.adapterz.jpa_practice.service.LikeService;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kr.adapterz.jpa_practice.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<GetLikeInfoResponseDto>> getLikes(
            @PathVariable Long postId
    ) {
        GetLikeInfoResponseDto result = likeService.getLikeInfo(postId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of("LIKE_RETRIEVER", result, null));
    }

    @PostMapping("/{userId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDto>> createLike(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody LikeRequestDto request
    ) {
        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("USER_MISMATCH");
        }
        LikeResponseDto result = likeService.pressLike(postId, userId, userDetails, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of("LIKE_CREATE", result, null));
    }

    @DeleteMapping("/{userId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDto>> deleteLike(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("USER_MISMATCH");
        }
        LikeResponseDto result = likeService.cancelLike(postId, userId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of("LIKE_DELETE", result, null));
    }
}
