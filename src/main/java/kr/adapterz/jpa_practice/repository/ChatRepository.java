package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import kr.adapterz.jpa_practice.entity.ChatRoom;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatRoom_RoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
    
    void deleteByChatRoom(ChatRoom chatRoom);
}
