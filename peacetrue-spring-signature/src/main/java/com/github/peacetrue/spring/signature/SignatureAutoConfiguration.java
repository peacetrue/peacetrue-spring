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

    public static final String CLIENT_SECRET_CODEC = "clientSecretCodec";

    @Bean
    @ConditionalOnMissingBean(name = CLIENT_SECRET_CODEC)
    public Codec clientSecretCodec() {
        return Codec.CHARSET_UTF8;
    }

    @Bean
    @ConditionalOnMissingBean(StringSignerFactory.class)
    public StringSignerFactory stringSignerFactory(@Qualifier(CLIENT_SECRET_CODEC) Codec clientSecretCodec) {
        return new CodecSignerFactory(BytesSignerFactory.HmacSHA256, clientSecretCodec);
    }

    @Bean
    @ConditionalOnMissingBean(ClientSecretProvider.class)
    @ConditionalOnExpression("'${peacetrue.signature.client-id:}'!='' && '${peacetrue.signature.client-secret:}'!=''")
    public ClientSecretProvider clientSecretProvider(SignatureProperties properties) {
        return clientId -> properties.getClientId().equals(clientId) ? properties.getClientSecret() : null;
    }

}
