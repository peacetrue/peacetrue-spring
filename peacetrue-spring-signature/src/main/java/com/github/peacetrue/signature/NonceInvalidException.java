package com.github.peacetrue.signature;

import lombok.Getter;

/**
 * 随机码无效异常。
 *
 * @author peace
 **/
@Getter
public class NonceInvalidException extends RuntimeException {

    private final String nonce;

    public NonceInvalidException(String nonce) {
        super(String.format("nonce %s already exists", nonce));
        this.nonce = nonce;
    }
}
