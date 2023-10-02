package com.github.peacetrue.signature;

import lombok.Getter;

/**
 * 随机码无效异常。
 *
 * @author peace
 **/
@Getter
public class NonceInvalidException extends RuntimeException {

    /** 随机码 */
    private final String nonce;

    /**
     * 属性设置实例化。
     *
     * @param nonce 随机码
     */
    public NonceInvalidException(String nonce) {
        super(String.format("nonce %s already exists", nonce));
        this.nonce = nonce;
    }
}
