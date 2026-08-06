package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.chat.ChatMessageRequest;
import kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse;
import kr.adapterz.jpa_practice.dto.chat.ChatRoomResponseDto;
import kr.adapterz.jpa_practice.entity.Chat;
import kr.adapterz.jpa_practice.entity.ChatRoom;
import kr.adapterz.jpa_practice.entity.User;
import kr.adapterz.jpa_practice.exception.NotFoundException;
import kr.adapterz.jpa_practice.repository.ChatRepository;
import kr.adapterz.jpa_practice.repository.ChatRoomRepository;
import kr.adapterz.jpa_practice.repository.UserRepository;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long chatRoomId, CustomUserDetails userDetails)
    {
        User sender = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        Chat chat = Chat.createTalkMessage(sender, chatRoom, request.getMessage()); // new 생성자없이 바로 메서드 사용이 가능?

        chatRepository.save(chat);

        return new ChatMessageResponse(chat);
    }

}
