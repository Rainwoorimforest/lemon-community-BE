package kr.adapterz.jpa_practice.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.UserRole;
import kr.adapterz.jpa_practice.exception.AccessDeniedException;
import kr.adapterz.jpa_practice.jwt.JwtTokenProvider;
import kr.adapterz.jpa_practice.dto.auth.TokenResponseDto;
import kr.adapterz.jpa_practice.dto.user.*;
import kr.adapterz.jpa_practice.entity.User;
import kr.adapterz.jpa_practice.exception.DuplicateException;
import kr.adapterz.jpa_practice.exception.NotFoundException;
import kr.adapterz.jpa_practice.exception.PasswordMismatchException;
import kr.adapterz.jpa_practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@Validated
@RequiredArgsConstructor // 생성자 자동 생성
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto createUser(UserRequestDto request, MultipartFile profileImgfile){

        // 비밀번호 확인 로직
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new PasswordMismatchException("PASSWORD_MISMATCH");
        }

        // 이메일 중복 검사
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("EMAIL_ALREADY_EXISTS");
        }

        // 닉네임 중복 검사
        if(userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new DuplicateException("NiCKNAME_ALREADY_EXISTS");
        }


        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                null, // request.getProfileImage(),
                null // 바로 아래 메소드로 대신 넣어줬음. user 엔티티가 만들어져야 다음 set함수가 동작하기 때문임.
        );

        uploadAndLinkImages(profileImgfile, user);
        user.setUserRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }

    public CurrentUserResponseDto getCurrentUser(CustomUserDetails userDetails) {
        Long loginUserId = userDetails.getUserId();

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND: ID " + loginUserId));

        return new CurrentUserResponseDto(user);
    }


    public UserAllResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        return new UserAllResponseDto(user);
    }

    public ProfileImageResponseDto getProfileImg(Long userId, CustomUserDetails userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        String profileImage = user.getProfileImage(); //TODO: null일수도있는데 optional로 보내야하나? dto에서 optional 안받잖아
        return new ProfileImageResponseDto(profileImage);
    }


    @Transactional
    public UserUpdateResponseDto updateUserInfo(
            @Positive Long userId, // @Positive는 userId가 0보다 큰 값인지를 검증
            CustomUserDetails userDetails,
            @Valid UserUpdateRequestDto request,
            MultipartFile profileImgfile
    ) {

        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        // 중복 닉네임 방지
        userRepository.findByNickname(request.getNickname()) // TODO: 프로필 이미지 null 확인과 본인 닉네임 확인 후 변경 코드가 Service에 잘 안보이도 하고 안정적이지 않아보임. 안티패턴인지 물어보기
                .ifPresent(existingUser -> {
                    if (!existingUser.getUserId().equals(userId)) {
                        throw new IllegalArgumentException("NiCKNAME_ALREADY_EXISTS");
                    }
                });

        uploadAndLinkImages(profileImgfile, user);
        user.changeNickname(request.getNickname());

        //TODO: nickname은 반정규화. 동기화 시켜야줘야 한다.
        // user 엔티티에서는 nickname이 변경됨. 근데 연관관계인 Post, Comment에는 복사해서 사용하였다. 이때 동기화 로직이 필요하다.

        return new UserUpdateResponseDto(user);
    }


    @Transactional
    public UserResponseDto updatePassword(
            @Positive Long userId,
            CustomUserDetails userDetails,
            @Valid PasswordUpdateRequestDto request
            ) {

        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new PasswordMismatchException("PASSWORD_MISMATCH");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        user.changePassword(passwordEncoder.encode(request.getPassword()));

        return new UserResponseDto(user);
    }


    @Transactional
    public UserResponseDto deleteUser(
            @Positive Long userId,
            CustomUserDetails userDetails
    ) {
        if (!userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        // 삭제하기 전에 먼저 user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        user.delete(); // 소프트딜리트

        return new UserResponseDto(user);
    }


    public TokenResponseDto login(LoginRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("PASSWORD_MISMATCH");
        }

        // 인증 성공 후 시큐리티 인증 정보 생성
        org.springframework.security.core.Authentication authentication =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_USER") // 시큐리티 권한 목록에 넣을때 ROLE_USER로 넣어줘야 한다 //enum으로 USER로 저장하긴했는데, autorities가 아니라 role로 저장
                );

        // 토큰 발행
        String accessToken = jwtTokenProvider.createToken(user.getEmail());

        return TokenResponseDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken("추후 구현 예정")
                    .userInfo(new UserResponseDto(user))
                    .build();
        }

    private void uploadAndLinkImages(MultipartFile file, User user) {
        if (file == null || file.isEmpty()) return;

        String imageUrl = s3Service.uploadFile(file);
        user.uploadProfileImage(imageUrl);
    }

}
