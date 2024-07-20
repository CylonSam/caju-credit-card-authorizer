package io.cylonsam.cajucreditcardauthorizer.infra.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${spring.data.redis.ttl}")
    private long MILLISECONDS_TTL;

    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void putValue(final String key, final Object value) {
        try {
            final String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, Duration.ofMillis(MILLISECONDS_TTL));
        } catch (final Exception e) {
            redisTemplate.delete(key);
            throw new RuntimeException(e);
        }
    }

    public <T> Optional<T> getValue(final String key, final Class<T> type) {
        final String jsonValue = redisTemplate.opsForValue().get(key);
        if (jsonValue == null || jsonValue.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(jsonValue, type));
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteValue(final String key) {
    redisTemplate.delete(key);
}
}
