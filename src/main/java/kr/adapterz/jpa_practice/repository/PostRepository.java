package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.PostImage;
import kr.adapterz.jpa_practice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // TODO: 동시성 문제 kevin에게 확인
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostInfo pi SET pi.viewCount = pi.viewCount + 1 WHERE pi.post.postId = :postId")
    int increaseViewCount(@Param("postId") Long postId);

}
