package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.ChatRoomParticipant;
import kr.adapterz.jpa_practice.entity.ChatRoomParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.adapterz.jpa_practice.entity.User;
import kr.adapterz.jpa_practice.entity.ChatRoom;

import java.util.List;

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, ChatRoomParticipantId> {
    
    boolean existsByUserAndChatRoom(User user, ChatRoom chatRoom);
    
    void deleteByUserAndChatRoom(User user, ChatRoom chatRoom);

    List<ChatRoomParticipant> findByChatRoom(ChatRoom chatRoom);

    void deleteByChatRoom(ChatRoom chatRoom);
}
