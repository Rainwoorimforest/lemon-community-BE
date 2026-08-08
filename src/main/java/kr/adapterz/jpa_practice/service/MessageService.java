package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.chat.ChatMessageRequest;
import kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse;
import kr.adapterz.jpa_practice.dto.chat.ChatRoomResponseDto;
import kr.adapterz.jpa_practice.entity.Chat;
import kr.adapterz.jpa_practice.entity.ChatRoom;
import kr.adapterz.jpa_practice.entity.User;
import kr.adapterz.jpa_practice.exception.NotFoundException;
import kr.adapterz.jpa_practice.redis.RedisPublisher;
import kr.adapterz.jpa_practice.repository.ChatRepository;
import kr.adapterz.jpa_practice.repository.ChatRoomRepository;
import kr.adapterz.jpa_practice.repository.UserRepository;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPublisher redisPublisher;
    private final ChannelTopic channelTopic;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long chatRoomId, Long senderId)
    {
        String lockKey = "chat:lock:" + request.getMessageId(); // 중복 방지 락

        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(lockKey, "LemonLocked", 30, TimeUnit.SECONDS); // 만약 이 키(Key)가 레디스에 없다면(Absent) 저장하고 true, 이미 있다면 저장하지 말고 false

        if (Boolean.FALSE.equals(isFirst)) {

            log.info("중복 메시지 방어됨(이미 방금 처리된 중복 메시지이므로 무시 (DB 저장 X, Publish X)): {}", request.getMessageId());
            return null; // 중단
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        Chat chat = Chat.createTalkMessage(sender, chatRoom, request.getMessage());

        chatRepository.save(chat);
        ChatMessageResponse response = new ChatMessageResponse(chat);

        redisPublisher.publish(channelTopic, response);

        return response;
    }

    @Transactional
    public ChatMessageResponse saveEnterMessage(ChatMessageRequest request, Long chatRoomId, Long senderId) {
        
        String lockKey = "chat:lock:" + request.getMessageId();
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 30, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(isFirst)) {
            log.info("중복 입장 메시지 방어됨(이미 방금 처리된 중복 메시지이므로 무시 (DB 저장 X, Publish X)): {}", request.getMessageId());
            return null; 
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        String enterMessage = sender.getNickname() + "님이 입장하셨습니다.";
        Chat chat = Chat.createSystemMessage(chatRoom, enterMessage);

        chatRepository.save(chat);

        ChatMessageResponse response = new ChatMessageResponse(chat);

        redisPublisher.publish(channelTopic, response);

        return response;
    }

    @Transactional
    public void saveLeaveMessage(Long chatRoomId, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        String leaveMessage = sender.getNickname() + "님이 퇴장하셨습니다.";
        Chat chat = Chat.createSystemMessage(chatRoom, leaveMessage);

        chatRepository.save(chat);

        ChatMessageResponse response = new ChatMessageResponse(chat);
        redisPublisher.publish(channelTopic, response);
    }

}
