package com.github.peacetrue.spring.signature;

import com.github.peacetrue.signature.ClientSecretProvider;
import com.github.peacetrue.signature.SignatureParameterValues;
import com.github.peacetrue.signature.SignatureParameterValuesGenerator;
import com.github.peacetrue.signature.StringSignerFactory;
import com.github.peacetrue.spring.http.client.PathMatcherClientHttpRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

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
    @ConditionalOnMissingBean(SignatureParameterValuesGenerator.class)
    public SignatureParameterValuesGenerator signaturePropertyValuesGenerator() {
        return SignatureParameterValues::random;
    }

    @Bean
    public SignatureClientHttpRequestInterceptor signatureClientHttpRequestInterceptor(
            SignatureProperties properties,
            SignatureParameterValuesGenerator propertyValuesGenerator,
            ClientSecretProvider clientSecretProvider,
            StringSignerFactory stringSignerFactory
    ) {
        return new SignatureClientHttpRequestInterceptor(new SignatureClientService(
                properties.getParameterNames(), properties.getClientId(),
                propertyValuesGenerator, clientSecretProvider,
                stringSignerFactory
        ));
    }

    @Bean
    public RestTemplateCustomizer signatureRestTemplateCustomizer(
            SignatureClientHttpRequestInterceptor interceptor, SignatureProperties properties) {
        return restTemplate -> restTemplate.getInterceptors().add(
                new PathMatcherClientHttpRequestInterceptor(
                        interceptor, Arrays.asList(properties.getSignPathPatterns())
                )
        );
    }

}
