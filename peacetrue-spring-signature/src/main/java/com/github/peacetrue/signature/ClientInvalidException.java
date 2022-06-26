package com.github.peacetrue.signature;

import lombok.Getter;

/**
 * 客户端无效异常。
 *
 * @author peace
 **/
@Getter
public class ClientInvalidException extends RuntimeException {

    private final String clientId;

    public ClientInvalidException(String clientId) {
        super(String.format("the clientId %s is invalid(no clientSecret found)", clientId));
        this.clientId = clientId;
    }
}
