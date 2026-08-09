# 🍋 Lemon 영어회화 플랫폼
데일리 영어 회화 표현을 피드로 공유하고 채팅으로 연습할 수 있는 _🍋레몬 영어회화 플랫폼_입니다.
공유하고 싶은 영어 표현을 게시하고, 팀원들과 표현을 자유롭게 연습해볼 수 있습니다.


## Back-end 소개

- Java Spring Boot로 서버 구축하였습니다.
- DB는 MySQL(기본 관계형 엔티티는 여기다 저장), Redis(레디스는 채팅 pub/sub으로 서비스확장을 고려, UUID 중복방어 멱등성 보장)
- 개발은 초기 프로젝트 설정부터, db 생성 및 연결, 서버 연결, 프론트엔드 연결까지 직접 구현했습니다
- mvc 패턴..?
- AWS EC2 인스턴스 생성(S3, RDS) 도커컴포즈로 

## 개발 인원 및 기간

- 개발기간 : 2026-05-29 ~ 2026-08-09
- 개발 인원 : 백엔드/프론트엔드 1명 (본인)

## 사용 기술 및 Tools

- Spring Boot MVC 패턴
- Redis pub/sub, Websocket STOMP 이용한 채팅
- AWS EC2+Docker로 배포. (도커에 3개인가 4개 컨테이너임 이건 너가 확인해줘. mysql, spring boot, react, + 아마 redis?)

## Front-end

- [프론트앤드 레포는 여기](https://github.com/Rainwoorimforest/lemon-community-FE)

## 서비스 시연 영상

- 

## 폴더 구조
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
</details>

## 시스템 아키텍처 (System Architecture)

![시스템 아키텍처](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white) 
전체 시스템은 **Docker Compose**를 통해 컨테이너화되어 배포 및 관리됩니다.

```mermaid
flowchart TB
    User((User))

    subgraph Docker ["🐳 Docker Compose Environment"]
        direction TB
        
        FE["💻 Frontend\n(React)"]
        BE["🍃 Backend\n(Spring Boot)"]
        
        subgraph DB ["Databases"]
            MySQL[("🐬 MySQL\n(Data Storage)")]
            Redis[("🔴 Redis\n(Cache & Pub/Sub)")]
        end
    end
    
    S3[("☁️ AWS S3\n(Image Storage)")]

    User -- "1. 웹 페이지 요청 (Port 80)" --> FE
    User -- "2. REST API / WebSocket (Port 8080)" --> BE
    User -- "3. 이미지 조회" --> S3
    
    BE <--> |"JPA (데이터 CRUD)"| MySQL
    BE <--> |"채팅 Pub/Sub 및 캐싱"| Redis
    BE --> |"이미지 파일 업로드"| S3
```

## ERD

## 서버 설계

(db mysql, springboot, redis, fe, docker 전체 구조 그림으로 구조도없나?)


### 서버 구조

Spring Boot 환경에 맞추어 API 경로(Route), Controller, Entity(Model)를 구조화하였습니다.

| | Route (API 경로) | Controller | Entity (Model) |
| --- | --- | --- | --- |
| **유저** | `/users` | `UserController` | `User` |
| **게시글** | `/posts` | `PostController` | `Post` |
| **댓글** | `/comments` | `CommentController` | `Comment` |
| **좋아요** | `/likes` | `LikeController` | `Like` |
| **채팅** | `/chatrooms`, `/messages` | `ChatRoomController`, `MessageController`| `ChatRoom`, `Chat` |
| **이미지** | `/images` | `ImageController` | `PostImage` |

<br>

### 구현 기능

#### Users
- **유저 CRUD 기능 구현**: 내 정보 조회, 회원가입, 회원정보 수정, 탈퇴 등
- **비밀번호 암호화**: 회원가입, 로그인, 비밀번호 변경 시 Spring Security의 `BCryptPasswordEncoder`를 사용하여 비밀번호를 안전하게 암호화 및 검증 처리
- **JWT 기반 인증**: 기존 세션 방식 대신 JWT(JSON Web Token)를 발급하여 쿠키(Cookie)에 저장하고 상태 저장 없이(Stateless) 유저 인증 유지
- **Security 미들웨어**: Spring Security와 커스텀 `JwtFilter`를 통해 유효한 토큰을 가진 유저 요청만 처리
- **이미지 업로드**: 프로필 이미지는 서버가 아닌 **AWS S3**에 업로드하여 저장하고, DB에는 반환된 이미지 URL만 저장하여 관리

#### Posts
- **게시글 CRUD 기능 구현**: 게시글 작성, 조회, 수정, 삭제 기능
- **인증된 접근 제어**: Security Context(`@AuthenticationPrincipal`)를 활용해 인증된 유저만 게시글 작성 및 본인 글 수정/삭제 가능하도록 처리
- **이미지 연동**: 다중 파일(MultipartFile) 업로드를 통해 S3에 게시글 이미지를 저장하고 Post와 매핑

#### Comments / Likes / Chat
- **댓글**: 게시글에 대한 댓글 작성, 수정, 삭제 등 댓글 CRUD 기능
- **좋아요**: 게시글 등에 대한 좋아요(Like) 추가 및 취소 기능
- **실시간 채팅**: WebSocket 및 Redis Pub/Sub 등을 활용한 채팅방 생성 및 실시간 메시지 송수신 기능

### 데이터베이스설계

## 트러블 슈팅
- [엔티티 연관관계 편의 메소드를 써야할때는?]()
- [cascade와 orphanremoval도 tradeoff]()


## 회고
..
