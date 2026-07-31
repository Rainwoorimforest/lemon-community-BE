package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.dto.chat.ChatMessageRequest;
import kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse;
import kr.adapterz.jpa_practice.service.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @MessageMapping("/chat.{chatRoomId}") // 브로커에 전달될때: /publish/chat.{chatRoomId}
    @SendTo("/subscribe/chat.{chatRoomId}") // 브로커가 구독자에게 전달할때(return): /publish/chat.{chatRoomId}
    public ChatMessageResponse sendMessage(
            ChatMessageRequest request,
            @DestinationVariable Long chatRoomId,
            Principal principal
        ) {

        log.info("메시지 전송: {}", request.getMessage());
        Authentication authentication = (Authentication) principal;
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 근데 이런거 service에서 해야한느거아닌가? 웹소켓 구현할때 서비스계층이 있긴한가
        String senderNickname = userDetails.getNickname();
        Long senderId = userDetails.getUserId();

        return new ChatMessageResponse(senderId, senderNickname, request.getMessage());
    }

    @MessageExceptionHandler
    public void handleException(RuntimeException e) {

        log.info("Exception: {}", e.getMessage());

    }
}
