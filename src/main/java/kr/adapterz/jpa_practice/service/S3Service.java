package kr.adapterz.jpa_practice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String awsRegion;

    public String uploadFile(MultipartFile multipartFile) {
        // Validate configuration
        if (bucketName == null || bucketName.isBlank()) {
            log.error("S3 bucket name is not configured (bucketName is null or empty)");
            throw new IllegalStateException("S3 bucket name is not configured");
        }
        if (awsRegion == null || awsRegion.isBlank()) {
            log.error("AWS region is not configured (awsRegion is null or empty)");
            throw new IllegalStateException("AWS region is not configured");
        }

        String original = Objects.requireNonNull(multipartFile.getOriginalFilename(), "Multipart file has no original filename");
        String ext = original.substring(original.lastIndexOf('.'));
        String key = UUID.randomUUID() + ext;

        long size = multipartFile.getSize();
        String contentType = multipartFile.getContentType();
        log.info("Uploading file to S3 - bucket: {}, region: {}, key: {}, size: {}, contentType: {}", bucketName, awsRegion, key, size, contentType);

        try (InputStream is = multipartFile.getInputStream()) {
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(size)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(putReq, RequestBody.fromInputStream(is, size));
        } catch (IOException e) {
            log.error("Failed to read multipart file input stream", e);
            throw new IllegalStateException("S3 upload failed due to I/O error", e);
        } catch (Exception e) {
            log.error("S3 upload encountered an exception", e);
            throw new IllegalStateException("S3 upload failed: " + e.getMessage(), e);
        }

        String s3Url = "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com/" + key;
        log.info("File uploaded successfully to {}", s3Url);
        return s3Url;
    }

    public void deleteFile(String s3Url) {
        if (s3Url == null || s3Url.isBlank()) return;
        
        try {
            // URL 형식: https://bucketName.s3.region.amazonaws.com/key
            String key = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteReq);
            log.info("Deleted file from S3: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", s3Url, e);
        }
    }
}
