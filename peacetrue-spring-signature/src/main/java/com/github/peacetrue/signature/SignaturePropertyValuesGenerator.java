package com.github.peacetrue.signature;

/**
 * 签名属性值生成器。
 *
 * @author peace
 **/
public interface SignaturePropertyValuesGenerator {

    /**
     * 生成签名属性值。
     *
     * @param clientId 客户端标志
     * @return 签名属性值
     */
    SignaturePropertyValues generate(String clientId);
}
