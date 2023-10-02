package com.github.peacetrue.signature;

import lombok.Getter;

/**
 * 签名无效异常。
 *
 * @author peace
 **/
@Getter
public class SignatureInvalidException extends RuntimeException {

    /** 签名 */
    private final String signature;
    /** 待签名消息 */
    private final String tobeSignedMessage;

    /**
     * 属性设置实例化。
     *
     * @param signature         签名
     * @param tobeSignedMessage 待签名消息
     */
    public SignatureInvalidException(String signature, String tobeSignedMessage) {
        super(String.format("signature %s verify failed (tobeSignedMessage: %s)", signature, tobeSignedMessage));
        this.signature = signature;
        this.tobeSignedMessage = tobeSignedMessage;
    }
}
