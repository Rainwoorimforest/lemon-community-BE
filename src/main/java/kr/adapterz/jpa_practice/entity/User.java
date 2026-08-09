package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; // TODO: Setter는 직접적으로 사용하지말고 불러올땐 메소드에 의도가 보이도록 합시다.
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Entity
@Getter @Setter
@SequenceGenerator(
        name = "user_seq", // 제너레이터 이름. @GeneratedValue에 쓰일거임
        sequenceName = "user_seq", // DB에 실제 존재하는 시퀀스 이름
        allocationSize = 30 // 커뮤니티 규모가 작다고 생각하여 30으로 잡음
)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 100) // 이후 해시를 이용할 것을 대비하여 100
    private String password;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole userRole;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) // defalut가 LAZY이지만 명시해준다.
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<ChatRoom> chatRoom = new ArrayList<>();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // private boolean isDeleted = false; // 기본값

    protected User() {}

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public User(String email, String password, String nickname, String profileImage, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.userRole = userRole;

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void uploadProfileImage(String profileImage) {

        this.profileImage = profileImage;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
