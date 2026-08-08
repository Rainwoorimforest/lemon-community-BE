package kr.adapterz.jpa_practice.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisher {

    private final org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public RedisPublisher(org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void publish(org.springframework.data.redis.listener.ChannelTopic topic, Object message) {
        try {
            // ObjectMapper를 통해 직접 JSON 문자열로 직렬화하여 StringRedisSerializer를 통과하도록 합니다.
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(topic.getTopic(), jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
