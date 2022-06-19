package com.github.peacetrue.spring.signature;

import com.github.peacetrue.signature.SignaturePropertyValues;
import com.github.peacetrue.signature.SignaturePropertyValuesGenerator;
import com.github.peacetrue.signature.Signer;
import com.github.peacetrue.spring.http.client.PathMatcherClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.peacetrue.spring.signature.SignatureAutoConfiguration.SIGNATURE_SIGNER;

/**
 * 签名客户端配置。
 *
 * @author peace
 **/
@Configuration
@AutoConfigureAfter(SignatureAutoConfiguration.class)
@ConditionalOnProperty(prefix = "peacetrue.signature", name = "sign-path-patterns")
@ConditionalOnClass(name = "org.springframework.boot.web.client.RestTemplateCustomizer")
public class SignatureClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SignaturePropertyValuesGenerator.class)
    public SignaturePropertyValuesGenerator signaturePropertyValuesGenerator() {
        return SignaturePropertyValues::random;
    }

    @Bean
    public SignatureClientHttpRequestInterceptor signatureClientHttpRequestInterceptor(
            @Qualifier(SIGNATURE_SIGNER) Signer<String, String> signatureSigner,
            SignaturePropertyValuesGenerator propertyValuesGenerator,
            SignatureProperties properties
    ) {
        return new SignatureClientHttpRequestInterceptor(new SignatureClientService(
                properties.getPropertyNames(),
                () -> propertyValuesGenerator.generate(properties.getClientId()),
                signatureSigner
        ));
    }

    @Bean
    public RestTemplateCustomizer signatureRestTemplateCustomizer(
            SignatureClientHttpRequestInterceptor interceptor, SignatureProperties properties) {
        return restTemplate -> restTemplate.getInterceptors().add(
                new PathMatcherClientHttpRequestInterceptor(
                        interceptor, properties.getSignPathPatterns()
                )
        );
    }

}
