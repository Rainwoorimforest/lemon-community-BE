package kr.adapterz.jpa_practice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisSubscriber {

    private final SimpMessageSendingOperations messagingTemplate; // 웹소켓 STOMP 브로커를 조종
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public RedisSubscriber(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 실행됨
    public void sendMessage(String publishMessage) {
        try {
            // String으로 받은 순수 JSON을 객체로 역직렬화
            kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse roomMessage = 
                objectMapper.readValue(publishMessage, kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse.class);

            // WebSocket 구독자들에게 메시지 Send (단일 서버 브로커 역할)
            messagingTemplate.convertAndSend("/subscribe/chat." + roomMessage.getChatRoomId(), roomMessage);

        } catch (Exception e) {
            log.error("Exception in RedisSubscriber: {}", e.getMessage());
        }
    }
}