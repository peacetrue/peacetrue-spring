package com.github.peacetrue.signature;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author peace
 **/
@Slf4j
class MemoryNonceVerifierTest {

    private static final EasyRandom EASY_RANDOM = new EasyRandom();

    @Test
    void basic() throws InterruptedException {
        Assertions.assertDoesNotThrow(() -> new MemoryNonceVerifier(2000));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MemoryNonceVerifier(-1, 200));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MemoryNonceVerifier(1000, 200));
        MemoryNonceVerifier verifier = new MemoryNonceVerifier(2, 50);
        Assertions.assertFalse(verifier.exists("test", "test"));
        Assertions.assertTrue(verifier.exists("test", "test"));
        Assertions.assertTrue(verifier.exists("test", "test"));
        TimeUnit.MILLISECONDS.sleep(52);
        Assertions.assertFalse(verifier.exists("test", "test"));
    }

    @Test
    void concurrence() throws InterruptedException {
        MemoryNonceVerifier verifier = new MemoryNonceVerifier(10, 500);
        TimeUnit.MICROSECONDS.sleep(501);

        List<String> clientIds = EASY_RANDOM.objects(String.class, 10).collect(Collectors.toList());
        List<String> nonces = EASY_RANDOM.objects(String.class, 10).collect(Collectors.toList());

        AtomicInteger absentCount = new AtomicInteger(0);
        each(verifier, clientIds, nonces, absentCount);
        Assertions.assertEquals(clientIds.size() * nonces.size(), absentCount.get());

        absentCount.set(0);
        Assertions.assertEquals(0, absentCount.get());

        TimeUnit.MILLISECONDS.sleep(1000);
        absentCount.set(0);
        each(verifier, clientIds, nonces, absentCount);
        Assertions.assertEquals(clientIds.size() * nonces.size(), absentCount.get());
    }

    private void each(MemoryNonceVerifier verifier,
                      List<String> clientIds, List<String> nonces,
                      AtomicInteger absentCount) throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        IntStream.range(0, threadCount)
                .mapToObj(i -> new Thread(() -> {
                    clientIds.forEach(clientId -> {
                        nonces.forEach(nonce -> {
                            if (!verifier.exists(clientId, nonce)) absentCount.incrementAndGet();
                        });
                    });
                    latch.countDown();
                }, "thread-" + i))
                .forEach(Thread::start);
        latch.await();
    }


}
