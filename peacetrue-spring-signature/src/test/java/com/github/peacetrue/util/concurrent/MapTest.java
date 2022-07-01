package com.github.peacetrue.util.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author peace
 **/
class MapTest {

    @Test
    void concurrenceHashMap() throws InterruptedException {
        Map<Integer, Integer> map = new HashMap<>();
        int threadCount = 100, loopCount = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        IntStream.range(0, threadCount)
                .mapToObj(i -> new Thread(() -> {
                    IntStream.range(0, loopCount).forEach(ii -> map.put(i * loopCount + ii, null));
                    latch.countDown();
                }, "thread-" + i))
                .forEach(Thread::start);
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
        Assertions.assertNotEquals(threadCount * loopCount, map.size());
    }


}
