package kr.adapterz.jpa_practice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j // 채팅 로그를 위한 어노테이션
public class WebSocketInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message); //TODO: 여기에 command 별로 로직을 작성(노션)
        StompCommand command = accessor.getCommand();
        if(command == StompCommand.SUBSCRIBE) {
            log.info(accessor.getDestination());
        }

        return message;
    }

}
