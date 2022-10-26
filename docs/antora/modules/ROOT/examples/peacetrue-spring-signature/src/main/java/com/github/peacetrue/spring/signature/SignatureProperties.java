package com.github.peacetrue.spring.signature;

import com.github.peacetrue.range.LongRange;
import com.github.peacetrue.signature.SignatureParameterNames;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 签名配置属性。
 *
 * @author peace
 **/
@Getter
@Setter
@ConfigurationProperties("peacetrue.signature")
public class SignatureProperties {

    /** 签名时使用的相关参数名 */
    @NestedConfigurationProperty
    private SignatureParameterNames parameterNames = new SignatureParameterNames();
    /** 客户端标志 */
    private String clientId;
    /** 客户端密钥。默认使用 UTF8 字符集解码，可切换为 Base64 或 Hex */
    private String clientSecret;
    /** 生成签名路径规则。生成签名时，拦截请求的路径规则，不配置此项不开启签名生成，拦截所有可配置为：/** */
    private String[] signPathPatterns;
    /** 验证签名路径规则。验证签名时，拦截请求的路径规则，不配置此项不开启签名验证，拦截所有可配置为：/** */
    private String[] verifyPathPatterns;
    /** 服务端允许的客户端时间偏移范围，单位毫秒 */
    @NestedConfigurationProperty
    private LongRange timestampOffset = new LongRange(-10_000L, 30_000L);

}
