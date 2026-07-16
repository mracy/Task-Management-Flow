package com.taskmanagement.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> void put(String key, T value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
            log.debug("Cache PUT: key={}", key);
        } catch (Exception e) {
            log.error("Cache PUT error for key {}: {}", key, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("Cache HIT: key={}", key);
                return Optional.of((T) value);
            }
            log.debug("Cache MISS: key={}", key);
        } catch (Exception e) {
            log.error("Cache GET error for key {}: {}", key, e.getMessage());
        }
        return Optional.empty();
    }

    public void evict(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Cache EVICT: key={}, deleted={}", key, deleted);
        } catch (Exception e) {
            log.error("Cache EVICT error for key {}: {}", key, e.getMessage());
        }
    }

    public void evictByPattern(String pattern) {
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Cache EVICT by pattern: {}, keys removed={}", pattern, keys.size());
            }
        } catch (Exception e) {
            log.error("Cache EVICT by pattern error for pattern {}: {}", pattern, e.getMessage());
        }
    }
}
