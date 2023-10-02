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

    /**
     * 构造随机签名参数值生成器。
     *
     * @return 随机签名参数值生成器
     */
    @Bean
    @ConditionalOnMissingBean(SignatureParameterValuesGenerator.class)
    public SignatureParameterValuesGenerator signaturePropertyValuesGenerator() {
        return SignatureParameterValues::random;
    }

    /**
     * 构造签名客户端请求拦截器。
     *
     * @param properties              签名属性
     * @param propertyValuesGenerator 签名参数值生成器
     * @param clientSecretProvider    客户端秘钥提供者
     * @param stringSignerFactory     字符串签名者工厂
     * @return 随机签名参数值生成器
     */
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

    /**
     * 构造签名 Rest 客户端请求自定义器。
     *
     * @param interceptor 签名客户端请求拦截器
     * @param properties  签名属性
     * @return 签名 Rest 客户端请求自定义器
     */
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
