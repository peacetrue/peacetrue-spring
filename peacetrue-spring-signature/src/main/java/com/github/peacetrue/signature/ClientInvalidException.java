package com.github.peacetrue.signature;

import lombok.Getter;

/**
 * 客户端无效异常。
 *
 * @author peace
 **/
@Getter
public class ClientInvalidException extends RuntimeException {

    /** 客户端标识 */
    private final String clientId;

    /**
     * 属性设置实例化。
     *
     * @param clientId 客户端标识
     */
    public ClientInvalidException(String clientId) {
        super(String.format("the clientId %s is invalid(no clientSecret found)", clientId));
        this.clientId = clientId;
    }
}
