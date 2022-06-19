package com.github.peacetrue.signature;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 基于内存的校验码验证者。单机环境使用，不支持分布式环境。
 *
 * @author peace
 **/
@Slf4j
public class MemoryNonceVerifier implements NonceVerifier {

    /** key 为客户端标志，value 为校验码、创建时间 */
    private final Map<String, Node> clients = new HashMap<>();
    private final long interval;
    private final long expired;

    /**
     * 构造内存校验码验证者，清理间隔为过期时间的 1/10，最小为 1 秒。
     *
     * @param expired 过期时间，校验码多长时间后过期，单位毫秒
     */
    public MemoryNonceVerifier(long expired) {
        this(Math.min(1_000, expired / 10), expired);
    }

    /**
     * 构造内存校验码验证者。
     *
     * @param interval 清理间隔，清除过期校验码的间隔，单位毫秒
     * @param expired  过期时间，校验码多长时间后过期，单位毫秒
     */
    public MemoryNonceVerifier(long interval, long expired) {
        if (interval <= 0) {
            throw new IllegalArgumentException("interval must > 0 milliseconds, actual " + interval);
        }
        if (expired < interval) {
            String message = String.format("expired must >= interval(%s milliseconds), actual %s", interval, expired);
            throw new IllegalArgumentException(message);
        }
        this.interval = interval;
        this.expired = expired;
    }

    @Override
    public synchronized boolean exists(String clientId, String nonce) {
        long currentTime = System.currentTimeMillis();
        Node node = this.clients.computeIfAbsent(clientId, temp -> new Node(currentTime, new LinkedList<>()));
        int expiredCount = removeExpired(clientId, node, currentTime);
        log.debug("remove expired nonce.size: {} -> {}", clientId, expiredCount);
        TimedNonce timedNonce = new TimedNonce(nonce, currentTime);
        if (node.nonces.contains(timedNonce)) return true;
        node.nonces.offer(timedNonce);
        log.trace("add expired nonce: {} -> {}", clientId, timedNonce);
        return false;
    }

    int removeExpired(String clientId, Node node, long currentTime) {
        if (currentTime - node.latestCleanTime <= interval) return 0;
        int count = 0;
        Queue<TimedNonce> nonces = node.nonces;
        TimedNonce oldest = nonces.peek();
        while (oldest != null && currentTime - oldest.createdTime > expired) {
            count++;
            nonces.poll();
            log.trace("remove expired nonce: {} -> {}", clientId, oldest);
            oldest = nonces.peek();
        }
        node.latestCleanTime = currentTime;
        return count;
    }

    @AllArgsConstructor
    static class Node {
        long latestCleanTime;
        Queue<TimedNonce> nonces;
    }

    @AllArgsConstructor
    static class TimedNonce {
        String nonce;
        long createdTime;

        @Override
        public boolean equals(Object o) {
            return nonce.equals(((TimedNonce) o).nonce);
        }

        @Override
        public String toString() {
            return nonce + '-' + createdTime;
        }
    }

}
