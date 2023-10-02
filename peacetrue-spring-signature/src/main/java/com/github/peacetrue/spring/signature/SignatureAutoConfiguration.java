package com.github.peacetrue.spring.signature;

import com.github.peacetrue.codec.Codec;
import com.github.peacetrue.signature.BytesSignerFactory;
import com.github.peacetrue.signature.ClientSecretProvider;
import com.github.peacetrue.signature.CodecSignerFactory;
import com.github.peacetrue.signature.StringSignerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 签名自动配置。
 *
 * @author peace
 **/
@Configuration
@EnableConfigurationProperties(SignatureProperties.class)
public class SignatureAutoConfiguration {

    /** 客户端秘钥编解码器名称 */
    public static final String CLIENT_SECRET_CODEC = "clientSecretCodec";

    /**
     * 构造默认的客户端秘钥编解码器。
     *
     * @return 字符集编解码器
     */
    @Bean
    @ConditionalOnMissingBean(name = CLIENT_SECRET_CODEC)
    public Codec clientSecretCodec() {
        return Codec.CHARSET_UTF8;
    }

    /**
     * 构造默认的字符串签名者工厂。
     *
     * @param clientSecretCodec 客户端秘钥编解码器
     * @return HmacSHA256 字符串签名者工厂
     */
    @Bean
    @ConditionalOnMissingBean(StringSignerFactory.class)
    public StringSignerFactory stringSignerFactory(@Qualifier(CLIENT_SECRET_CODEC) Codec clientSecretCodec) {
        return new CodecSignerFactory(BytesSignerFactory.HmacSHA256, clientSecretCodec);
    }

    /**
     * 通过签名属性构造客户端秘钥提供者。
     *
     * @param properties 签名属性
     * @return 客户端秘钥提供者
     */
    @Bean
    @ConditionalOnMissingBean(ClientSecretProvider.class)
    @ConditionalOnExpression("'${peacetrue.signature.client-id:}'!='' && '${peacetrue.signature.client-secret:}'!=''")
    public ClientSecretProvider clientSecretProvider(SignatureProperties properties) {
        return clientId -> properties.getClientId().equals(clientId) ? properties.getClientSecret() : null;
    }

}
