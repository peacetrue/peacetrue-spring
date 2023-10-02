package com.github.peacetrue.spring.signature;

import com.github.peacetrue.range.LongRange;
import com.github.peacetrue.signature.ClientSecretProvider;
import com.github.peacetrue.signature.MemoryNonceVerifier;
import com.github.peacetrue.signature.NonceVerifier;
import com.github.peacetrue.signature.StringSignerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 签名服务端配置。
 *
 * @author peace
 **/
@Configuration
@AutoConfigureAfter(SignatureAutoConfiguration.class)
@ConditionalOnProperty(name = "peacetrue.signature.verify-path-patterns")
public class SignatureServerAutoConfiguration {

    @Autowired
    private SignatureProperties properties;

    /**
     * 注册默认的内存随机码验证器。
     *
     * @return 内存随机码验证器
     */
    @Bean
    @ConditionalOnMissingBean(NonceVerifier.class)
    public NonceVerifier nonceVerifier() {
        return new MemoryNonceVerifier(LongRange.getOffset(properties.getTimestampOffset()));
    }

    /**
     * 注册默认的签名服务端服务。
     *
     * @param clientSecretProvider 客户端秘钥提供者
     * @param stringSignerFactory  字符串签名者工厂
     * @param nonceVerifier        随机码验证器
     * @return 签名服务端服务
     */
    @Bean
    public SignatureServerService signatureServerService(ClientSecretProvider clientSecretProvider,
                                                         StringSignerFactory stringSignerFactory,
                                                         NonceVerifier nonceVerifier) {
        return new SignatureServerService(
                properties.getParameterNames(), clientSecretProvider,
                stringSignerFactory, properties.getTimestampOffset(), nonceVerifier
        );
    }

    /**
     * 处理器拦截器配置。
     */
    @Configuration
    public static class HandlerInterceptorConfiguration {
        /**
         * 注册签名服务端拦截器。
         *
         * @param properties             签名属性
         * @param signatureServerService 签名服务端服务
         * @return 配置者
         */
        @Bean
        public WebMvcConfigurer signatureServerWebMvcConfigurer(
                SignatureProperties properties, SignatureServerService signatureServerService
        ) {
            return new WebMvcConfigurer() {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    String[] patterns = properties.getVerifyPathPatterns();
                    registry.addInterceptor(new SignatureHandlerInterceptor(signatureServerService)).addPathPatterns(patterns);
                }
            };
        }
    }

}
