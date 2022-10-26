package com.github.peacetrue.signature;

import javax.annotation.Nullable;

/**
 * 客户端秘钥提供者。
 *
 * @author peace
 **/
public interface ClientSecretProvider {

    /**
     * 获取客户端秘钥。
     *
     * @param clientId 客户端标志
     * @return 客户端秘钥
     */
    @Nullable
    String getClientSecret(String clientId);

}
