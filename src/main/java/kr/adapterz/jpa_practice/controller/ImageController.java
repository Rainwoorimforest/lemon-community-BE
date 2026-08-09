package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.response.ApiResponse;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import kr.adapterz.jpa_practice.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    // 게시글 이미지, 프로필 이미지 통합.

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        String s3Url = s3Service.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.of("IMAGE_UPLOAD_SUCCESS", s3Url));
    }

    // TODO: 삭제하는 api controller 생성. 현재는 이미지 생성만 가능(삭제는 게시글 삭제될때)


}
