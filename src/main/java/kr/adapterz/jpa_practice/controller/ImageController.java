package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.response.ApiResponse;
import kr.adapterz.jpa_practice.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        String s3Url = s3Service.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.of("IMAGE_UPLOAD_SUCCESS", s3Url));
    }

    // 삭제하는 api controller를 안만들었네
}
