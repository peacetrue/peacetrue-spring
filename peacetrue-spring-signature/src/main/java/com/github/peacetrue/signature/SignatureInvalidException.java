package com.github.peacetrue.signature;

import lombok.Getter;

/**
 * 签名无效异常。
 *
 * @author peace
 **/
@Getter
public class SignatureInvalidException extends RuntimeException {

    private final String signature;
    private final String tobeSignedMessage;

    public SignatureInvalidException(String signature, String tobeSignedMessage) {
        super(String.format("signature %s verify failed (tobeSignedMessage: %s)", signature, tobeSignedMessage));
        this.signature = signature;
        this.tobeSignedMessage = tobeSignedMessage;
    }
}
