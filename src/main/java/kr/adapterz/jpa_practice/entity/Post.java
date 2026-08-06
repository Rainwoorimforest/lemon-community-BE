package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; // TODO: Setter는 직접적으로 사용하지말고 불러올땐 메소드에 의도가 보이도록 합시다.
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import kr.adapterz.jpa_practice.repository.CommentRepository;
import kr.adapterz.jpa_practice.repository.LikeRepository;

import kr.adapterz.jpa_practice.entity.Comment;

@Entity
@Getter @Setter
@SequenceGenerator(
        name = "post_seq",
        sequenceName = "post_seq",
        allocationSize = 30
)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(nullable = false, length = 26)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Like> likes = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private PostInfo postInfo;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImage> postImages = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRoom = new ArrayList<>();

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // TODO: soft delete 구현
//    @Column(name = "deleted_at")
//    private LocalDateTime deletedAt;

    protected Post() {}

    // 이미지는 생성자에서 빼고 메소드만 따로 둔다
    public Post(String title, String content, User author) {
        this.nickname = author.getNickname(); //얘는 복사임
        this.author = author;// 외래키인데
        this.title = title;
        this.content = content;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public List<Comment> getComments() {return comments; }

    public List<Like> getLikes() {return likes;}

    public void linkPostInfo(PostInfo postInfo) { this.postInfo = postInfo; }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void addPostImage(String url) {
        PostImage image = new PostImage(url, this);
        postImages.add(image);
    }


    public void checkAndUpdateNickname(){
        if(!this.nickname.equals(this.author.getNickname())) // 확인했는데 원래 저장했던 닉네임이랑 현재 User 엔티티가 가진 닉네임이랑 다르면 업데이트해라
        {
            this.nickname = this.author.getNickname();
        }
    }

}
