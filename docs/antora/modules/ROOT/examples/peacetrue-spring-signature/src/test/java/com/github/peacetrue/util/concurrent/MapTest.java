package com.github.peacetrue.util.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.metadata.ConfigurationMetadata;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;
import org.springframework.boot.configurationprocessor.metadata.JsonMarshaller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author peace
 **/
class MapTest {

    @Test
    void concurrenceHashMap() throws InterruptedException {
        Map<Integer, Integer> map = new HashMap<>();
        int threadCount = 100, loopCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        IntStream.range(0, threadCount)
                .mapToObj(i -> new Thread(() -> {
                    IntStream.range(0, loopCount).forEach(ii -> map.put(i * loopCount + ii, null));
                    latch.countDown();
                }, "thread-" + i))
                .forEach(Thread::start);
        latch.await();
        Assertions.assertNotEquals(threadCount * loopCount, map.size());
    }


}
