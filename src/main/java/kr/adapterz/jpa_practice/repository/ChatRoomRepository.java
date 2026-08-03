package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
