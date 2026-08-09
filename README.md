# 🍋 Lemon 영어회화 플랫폼

## Back-end 소개

- 
- 
- 
- 

### 개발 인원 및 기간

- 개발기간 : 
- 개발 인원 : 

### 사용 기술 및 Tools

- 
- 
- 

### Front-end

- [Front-end Github]()

### 서비스 시연 영상

- 

### 폴더 구조
<details>
<summary><b>📂 백엔드 폴더 구조 보기 (Spring Boot)</b></summary>
<div markdown="1">

```text
📦 jpa_practice (Backend)
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java/kr/adapterz/jpa_practice
 ┃ ┃ ┃ ┣ 📂 config           # CORS, WebSocket 등 설정 클래스 모음
 ┃ ┃ ┃ ┣ 📂 controller       # REST API 엔드포인트 (ChatRoom, Post, User 등)
 ┃ ┃ ┃ ┣ 📂 dto              # 클라이언트 요청/응답 데이터 전송 객체 (DTO)
 ┃ ┃ ┃ ┣ 📂 entity           # JPA 엔티티 클래스 (DB 테이블 매핑)
 ┃ ┃ ┃ ┣ 📂 exception        # 커스텀 예외 클래스 및 전역 에러 처리 (GlobalExceptionHandler)
 ┃ ┃ ┃ ┣ 📂 handler          # 웹소켓(WebSocket) 및 기타 핸들러
 ┃ ┃ ┃ ┣ 📂 jwt              # JWT 토큰 생성, 검증 로직 및 필터
 ┃ ┃ ┃ ┣ 📂 redis            # Redis 캐싱 및 세션 관리 설정
 ┃ ┃ ┃ ┣ 📂 repository       # Spring Data JPA 리포지토리 인터페이스 (DB 접근)
 ┃ ┃ ┃ ┣ 📂 response         # API 공통 응답 포맷 (통일된 JSON 응답 반환)
 ┃ ┃ ┃ ┣ 📂 security         # Spring Security 설정 (BCrypt 등 암호화/인증)
 ┃ ┃ ┃ ┣ 📂 service          # 핵심 비즈니스 로직 처리 (ChatRoom, Post, User 등)
 ┃ ┃ ┃ ┗ 📜 JpaPracticeApplication.java # 스프링 부트 실행 진입점
 ┃ ┃ ┗ 📂 resources
 ┃ ┃   ┗ 📜 application.yml  # 데이터베이스 연결, JWT 비밀키 등 환경 설정
 ┣ 📜 build.gradle           # 의존성 라이브러리 및 빌드 관리 파일
 ┗ 📜 README.md
```

## 서버 설계

### 서버 구조

| 구분 |  |  |  |
| ---- | ---- | ---- | ---- |
|  |  |  |  |
|  |  |  |  |
|  |  |  |  |
|  |  |  |  |

### 구현 기능

### 데이터베이스설계

### 트러블 슈팅

### 회고

