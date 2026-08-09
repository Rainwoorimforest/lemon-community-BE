package kr.adapterz.jpa_practice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.jpa_practice.dto.user.*;
import kr.adapterz.jpa_practice.dto.auth.TokenResponseDto;
import kr.adapterz.jpa_practice.response.ApiResponse;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import kr.adapterz.jpa_practice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import kr.adapterz.jpa_practice.jwt.JwtCookieConstants;
import org.springframework.web.multipart.MultipartFile;


// @CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponseDto>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        CurrentUserResponseDto result = userService.getCurrentUser(userDetails);
        return ResponseEntity.ok(
                ApiResponse.of("USER_AUTH", result)
        );
    }

    // 회원정보 페이지 조회 시 유저 정보 반환
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserAllResponseDto>> getUser(
            @PathVariable Long userId
    ) {
        UserAllResponseDto result = userService.getUser(userId);
        return ResponseEntity.ok(
                ApiResponse.of("USER_RETRIEVED", result)
        );
    }

    // 프로필 이미지 가져오기
    @GetMapping("/profileImage/{userId}")
    public ResponseEntity<ApiResponse<ProfileImageResponseDto>> getProfileImg(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProfileImageResponseDto result = userService.getProfileImg(userId, userDetails);

        return ResponseEntity.ok(ApiResponse.of("USER_PROFILE_IMAGE_RETRIEVED", result));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser (
            @Valid @RequestPart("request") UserRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        UserResponseDto result = userService.createUser(request, file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/users/signup" + result.getUserId())
                .body(ApiResponse.of("USER_CREATED",result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDto>> login (
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse response
    ) {
        TokenResponseDto tokenResult = userService.login(request);


        // 쿠키 설정하기
        ResponseCookie cookie = ResponseCookie.from(JwtCookieConstants.ACCESS_TOKEN_COOKIE_NAME, tokenResult.getAccessToken())
                .httpOnly(true) // 스프링시큐리티는 csrf token에 대한 httpONly 설정이고, 이거는 jwt token에 대한 httpOnly 설정
                .secure(false) // 로컬 HTTP 개발환경
                .sameSite("Lax") // localhost:5500 → localhost:8080
                .path("/")
                .maxAge(60 * 30) // JWT 유효기간과 동일하게
                .build();


        // response에 쿠키 헤더 추가
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of("LOGIN_SUCCESS", tokenResult.getUserInfo()));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserUpdateResponseDto>> updateNicknameProfileImg(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart("request") UserUpdateRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        UserUpdateResponseDto result = userService.updateUserInfo(userId, userDetails, request, file);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("NICKNAME_IMAGE_UPDATED", result));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<UserResponseDto>> updatePassword(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequestDto request
    ) {
        UserResponseDto result = userService.updatePassword(userId, userDetails, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("PASSWORD_UPDATED", result));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponseDto result = userService.deleteUser(userId, userDetails);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_DELETED", result));
    }
}
