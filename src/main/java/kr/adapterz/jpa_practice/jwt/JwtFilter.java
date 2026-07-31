package kr.adapterz.jpa_practice.jwt;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import kr.adapterz.jpa_practice.jwt.JwtTokenProvider;
import kr.adapterz.jpa_practice.jwt.JwtCookieConstants;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; // JWT 생성/검증 로직이 든 클래스

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 토큰 유효성 검사
        if (StringUtils.hasText(token)
                && jwtTokenProvider.validateToken(token)) {

            Authentication authentication =
                    jwtTokenProvider.getAuthentication(token);

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            return null;
        }

        for(Cookie cookie : cookies) {
            if (JwtCookieConstants.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()) // 여기를 이어줘야해 어케불러오지?
                && StringUtils.hasText(cookie.getValue())) {
                return cookie.getValue();
            }
        }

        return null;

    }
}