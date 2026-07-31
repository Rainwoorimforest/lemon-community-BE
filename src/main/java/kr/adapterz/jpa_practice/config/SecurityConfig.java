package kr.adapterz.jpa_practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import kr.adapterz.jpa_practice.jwt.JwtFilter;
import kr.adapterz.jpa_practice.jwt.JwtExceptionFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtFilter jwtFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 스프링 시큐리티의 CookieCsrfTokenRepository는 기본적으로 보안을 위해 HttpOnly 속성이 true(자바스크립트 접근 불가능)로 설정됨. 근데 아래 경로만큼은 일시적으로 HttpOnly 속성을 false하겠다.
//                        .ignoringRequestMatchers("/users/signup", "/users/login")) // 해당 경로에 대해서는 CSRF 검사 제외

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 세션 설정: STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/users/signup", "/users/login", "/error", "/ws/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 필터 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtFilter.class);



        return http.build();
    }
}
