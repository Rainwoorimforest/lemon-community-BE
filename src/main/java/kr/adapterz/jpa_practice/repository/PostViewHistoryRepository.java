package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.PostViewHistory;
import kr.adapterz.jpa_practice.entity.PostViewHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewHistoryRepository extends JpaRepository<PostViewHistory, PostViewHistoryId> {
}