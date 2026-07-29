package kr.adapterz.jpa_practice.config;

import kr.adapterz.jpa_practice.dto.chat.ChatMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketConnectionTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testWebSocketConnectionAndSendReceive() throws Exception {
        // Given
        String wsUrl = "ws://localhost:" + port + "/ws";
        BlockingQueue<ChatMessageResponse> blockingQueue = new LinkedBlockingDeque<>();

        // When - 연결 시도
        System.out.println("====== [TEST] 웹소켓 연결 시도 중 (" + wsUrl + ") ======");
        StompSession session = stompClient
                .connectAsync(wsUrl, new StompSessionHandlerAdapter() {
                    @Override
                    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                        System.out.println("====== [TEST ERROR] 연결 중 예외 발생: " + exception.getMessage() + " ======");
                    }
                })
                .get(5, TimeUnit.SECONDS);

        // Then - 연결 성공 검증
        assertThat(session.isConnected()).isTrue();
        System.out.println("====== [TEST SUCCESS] 웹소켓 3-Way Handshake 및 STOMP 연결 성공! ======");

        // And - 채팅방 구독 (/subscribe/chat.1)
        session.subscribe("/subscribe/chat.1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("====== [TEST SUCCESS] 브로커로부터 메시지 수신 완료! ======");
                blockingQueue.offer((ChatMessageResponse) payload);
            }
        });

        // And - 메시지 송신 (/publish/chat.1)
        Map<String, String> requestPayload = Map.of(
                "sender", "레이첼",
                "message", "안녕하세요"
        );
        
        System.out.println("====== [TEST] 메시지 발행 시도: /publish/chat.1 ======");
        session.send("/publish/chat.1", requestPayload);

        // Then - 구독한 곳에서 메시지 정상 수신했는지 검증
        ChatMessageResponse response = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertThat(response).isNotNull();
        assertThat(response.getSender()).isEqualTo("레이첼");
        assertThat(response.getMessage()).isEqualTo("안녕하세요");
        
        System.out.println("====== [TEST SUCCESS] 수신된 응답 검증 완료 -> sender: " + response.getSender() + ", message: " + response.getMessage() + " ======");
        
        session.disconnect();
    }
}
