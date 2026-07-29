package kr.adapterz.jpa_practice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor // final로 선언한 필드를 생성자 매개변수로 주입해줌
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;

    // 3핸드셰이크 이후 HTTP에서 WebSocket으로 업그레이드 하여 양방향 통신을 하게하는 첫 진입점
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
//                .setAllowedOrigins("http://localhost:5500/",
//                                    "http://127.0.0.1:5500/");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/subscribe"); // 브로커가 구독하는 유저에게 전달 //topic
        config.setApplicationDestinationPrefixes("/publish"); // 브로커 아니고, 스프링 controller로 전달 //app
    }
}
