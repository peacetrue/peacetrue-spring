package com.github.peacetrue.spring.signature;

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

    @Bean
    @ConditionalOnMissingBean(NonceVerifier.class)
    public NonceVerifier nonceVerifier() {
        return new MemoryNonceVerifier(properties.getTimestampOffset().getOffset());
    }

    @Bean
    public SignatureServerService signatureServerService(ClientSecretProvider clientSecretProvider,
                                                         StringSignerFactory stringSignerFactory,
                                                         NonceVerifier nonceVerifier) {
        return new SignatureServerService(
                properties.getParameterNames(), clientSecretProvider,
                stringSignerFactory, properties.getTimestampOffset(), nonceVerifier
        );
    }

    @Configuration
    public static class HandlerInterceptorConfiguration {
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
