package com.github.peacetrue.spring.signature;

import com.github.peacetrue.signature.NonceVerifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.redis.connection.RedisStringCommands.SetOption.SET_IF_ABSENT;

/**
 * 基于 Redis 的校验码验证者。
 *
 * @author peace
 **/
public class RedisNonceVerifier implements NonceVerifier {

    private final RedisTemplate<?, ?> redisTemplate;
    private final Expiration expiration;

    public RedisNonceVerifier(RedisTemplate<?, ?> redisTemplate, Long expiration) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
        this.expiration = Expiration.from(Objects.requireNonNull(expiration), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean exists(String clientId, String nonce) {
        String key = String.format("NonceVerifier_%s_%s", clientId, nonce);
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        return !Boolean.TRUE.equals(redisTemplate.execute(
                (RedisCallback<Boolean>) connection -> connection.set(bytes, new byte[0], expiration, SET_IF_ABSENT)
        ));
    }

}
