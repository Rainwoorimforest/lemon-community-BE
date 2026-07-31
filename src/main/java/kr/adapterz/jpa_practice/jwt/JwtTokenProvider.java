package kr.adapterz.jpa_practice.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;
    private final UserDetailsService userDetailsService;
    private final long ACCESS_TOKEN_TIME = 1000 * 60 * 30; // 30분

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    // 토큰 생성
    public String createToken(String email) {
        Date now = new Date();

        Date expire = new Date(now.getTime() + ACCESS_TOKEN_TIME);

        return Jwts.builder() // Builder 객체 생성
                .subject(email)
                .issuedAt(now)
                .expiration(expire)
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 이메일 추출(토큰의 Header, Payload, Signature 중에서 Payload에서 이메일 추출)
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        return true;
    }

    // Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmail(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // 이미 DB를 조회하여 유저 정보를 다 들고 있는 UserDetails 객체를 가져옵

        return new UsernamePasswordAuthenticationToken( // 여기에 유저 정보(userDetails)가 통째로 할당
                userDetails,
                "",
                userDetails.getAuthorities());
    }

    // Bearer 제거: Authorization 헤더에서 JWT 추출
//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//
//        if (bearerToken != null &&
//                bearerToken.startsWith("Bearer ")) {
//
//            return bearerToken.substring(7);
//        }
//
//        return null;
//    }
}