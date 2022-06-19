package com.github.peacetrue.signature;

import javax.annotation.Nullable;

/**
 * 签名提供者。
 *
 * @author peace
 **/
public interface SignerProvider {

    /**
     * 获取签名提供者。
     *
     * @param clientId 客户端标志
     * @return 签名提供者
     */
    @Nullable
    Signer<String, String> findSigner(String clientId);

}
