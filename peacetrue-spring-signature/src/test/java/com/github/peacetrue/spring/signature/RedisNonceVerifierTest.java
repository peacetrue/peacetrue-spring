package com.github.peacetrue.spring.signature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.redis.connection.RedisStringCommands.SetOption.SET_IF_ABSENT;

/**
 * @author peace
 **/
@ExtendWith(MockitoExtension.class)
class RedisNonceVerifierTest {

    @Mock
    private RedisConnection redisConnection;
    @Mock
    private RedisConnectionFactory redisConnectionFactory;
    private RedisNonceVerifier redisNonceVerifier;

    @BeforeEach
    void setUp() {
        Mockito.when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        redisNonceVerifier = new RedisNonceVerifier(redisTemplate, 1000L);
    }

    @Test
    void exists() {
        String clientId = "clientId";
        String nonce = "nonce";
        Mockito.when(redisConnection.set(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true, false);
        Assertions.assertFalse(redisNonceVerifier.exists(clientId, nonce));
        Assertions.assertTrue(redisNonceVerifier.exists(clientId, nonce));
    }
}
