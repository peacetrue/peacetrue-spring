package com.github.peacetrue.signature;

import com.github.peacetrue.range.LongRange;
import lombok.Getter;

import java.util.Objects;

/**
 * 时间戳无效异常，客户端时间搓不在服务端许可范围内。
 *
 * @author peace
 **/
@Getter
public class TimestampInvalidException extends RuntimeException {

    /** 客户端时间戳 */
    private final long clientTimestamp;
    /** 服务端时间戳 */
    private final long serverTimestamp;
    /** 服务端允许的时间戳偏移 */
    private final LongRange timestampOffset;

    /**
     * 属性设置实例化。
     *
     * @param clientTimestamp 客户端时间戳
     * @param serverTimestamp 服务端时间戳
     * @param timestampOffset 时间戳偏移范围
     */
    public TimestampInvalidException(long clientTimestamp, long serverTimestamp, LongRange timestampOffset) {
        super(message(clientTimestamp, serverTimestamp, timestampOffset));
        this.clientTimestamp = clientTimestamp;
        this.serverTimestamp = serverTimestamp;
        this.timestampOffset = Objects.requireNonNull(timestampOffset);
    }

    private static String message(long clientTimestamp, long serverTimestamp, LongRange timestampOffset) {
        return String.format(
                "client timestamp %s is not in %s which defined by server timestamp %s with offset %s",
                clientTimestamp, timestampOffset.increase(serverTimestamp), serverTimestamp, timestampOffset
        );
    }
}
