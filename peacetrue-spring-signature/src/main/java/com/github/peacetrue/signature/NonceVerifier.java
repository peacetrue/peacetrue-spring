package com.github.peacetrue.signature;

/**
 * 校验码验证者。
 *
 * @author peace
 **/
public interface NonceVerifier {

    /**
     * 校验码是否存在。
     *
     * @param clientId 客户端标志
     * @param nonce    唯一随机校验码
     * @return {@code true} 如果已存在，否则 {@code false}
     */
    boolean exists(String clientId, String nonce);

}
