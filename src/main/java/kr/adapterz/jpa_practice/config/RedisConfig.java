package kr.adapterz.jpa_practice.config;

import kr.adapterz.jpa_practice.redis.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // 단일 채널(Topic) 사용
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chatroom");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic channelTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    // 실제 메시지를 수신하여 처리할 클래스(RedisSubscriber) 지정
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        // MessageListenerAdapter는 기본적으로 StringRedisSerializer를 사용합니다.
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }

    // 애플리케이션에서 사용할 RedisTemplate 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Value 직렬화 (이중 직렬화 방지를 위해 반드시 StringRedisSerializer 사용)
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}