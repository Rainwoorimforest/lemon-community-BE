package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.dto.chat.ChatMessageRequest;
import kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @MessageMapping("/chat.{chatRoomId}") // 브로커에 전달될때: /publish/chat.{chatRoomId}
    @SendTo("/subscribe/chat.{chatRoomId}") // 브로커가 구독자에게 전달할때(return): /publish/chat.{chatRoomId}
    public ChatMessageResponse sendMessage(ChatMessageRequest request, @DestinationVariable Long chatRoomId) {

        // TODO: DB 저장, jwt 토큰(interceptor에서 jwt검사야? 아님 여기서 Principal에서야?)
        log.info("메시지 전송: {}", request.getMessage());

        return new ChatMessageResponse(request.getSender(), request.getMessage());
    }

    @MessageExceptionHandler
    public void handleException(RuntimeException e) {
        log.info("Exception: {}", e.getMessage());
    }
}
