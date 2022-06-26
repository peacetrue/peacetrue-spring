package com.github.peacetrue.signature;

/**
 * 签名参数值生成器。
 *
 * @author peace
 **/
public interface SignatureParameterValuesGenerator {

    /**
     * 生成签名参数值。
     *
     * @param clientId 客户端标志
     * @return 签名参数值
     */
    SignatureParameterValues generateSignatureParameterValues(String clientId);
}
