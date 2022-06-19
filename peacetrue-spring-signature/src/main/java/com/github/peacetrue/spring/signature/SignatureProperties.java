package com.github.peacetrue.spring.signature;

import com.github.peacetrue.range.LongRange;
import com.github.peacetrue.signature.SignaturePropertyNames;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * 签名配置属性。
 *
 * @author peace
 **/
@Getter
@Setter
@ConfigurationProperties("peacetrue.signature")
public class SignatureProperties {

    /** 客户端标志 */
    private String clientId;
    /** 密钥，使用 HmacSHA256 算法，生成和验证签名时的密钥，默认使用 HEX 解码 */
    private String secretKey;
    /** 私钥，使用 SHA256WithRSA 算法，生产签名时的私钥，默认使用 HEX 解码 */
    private String privateKey;
    /** 公钥，使用 SHA256WithRSA 算法，验证签名时的公钥，默认使用 HEX 解码 */
    private String publicKey;
    /** 签名路径规则，生成签名时，拦截请求的路径规则，不配置此项不开启签名生成，拦截所有可配置为：/** */
    private List<String> signPathPatterns;
    /** 验签路径规则，验证签名时，拦截请求的路径规则，不配置此项不开启签名验证，拦截所有可配置为：/** */
    private String[] verifyPathPatterns;
    /** 签名时使用的相关属性名 */
    @NestedConfigurationProperty
    private SignaturePropertyNames propertyNames = new SignaturePropertyNames();
    /** 客户端时间与服务端时间允许的偏移范围，单位毫秒 */
    @NestedConfigurationProperty
    private LongRange timestampOffset = new LongRange(-10_000L, 30_000L);

}
