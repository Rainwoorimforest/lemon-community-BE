package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.dto.chat.ChatMessageRequest;
import kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import kr.adapterz.jpa_practice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    @MessageMapping("/chat.{chatRoomId}") // 브로커에 전달될때: /publish/chat.{chatRoomId}
   // @SendTo("/subscribe/chat.{chatRoomId}") // 브로커가 구독자에게 전달할때(return): /publish/chat.{chatRoomId}
    public void sendMessage(
            ChatMessageRequest request,
            @DestinationVariable Long chatRoomId,
            Principal principal
        ) {

        log.info("메시지 전송: {}", request.getMessage());

        Long senderId;
        if (principal != null) {
            Authentication authentication = (Authentication) principal;
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            senderId = userDetails.getUserId();
        } else {
            senderId = request.getSenderId(); // 쿠키 누락 시 폴백
        }

        messageService.saveMessage(request, chatRoomId, senderId);

    }

    @MessageMapping("/chat.enter.{chatRoomId}") // 입장 시 호출될 경로
    public void enterMessage(
            ChatMessageRequest request,
            @DestinationVariable Long chatRoomId,
            Principal principal
        ) {

        log.info("입장 메시지 요청");

        Long senderId;
        if (principal != null) {
            Authentication authentication = (Authentication) principal;
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            senderId = userDetails.getUserId();
        } else {
            senderId = request.getSenderId(); // 쿠키 누락 시 폴백
        }

        messageService.saveEnterMessage(request, chatRoomId, senderId);
    }

    @MessageExceptionHandler
    public void handleException(RuntimeException e) {

        log.info("Exception: {}", e.getMessage());

    }
}
