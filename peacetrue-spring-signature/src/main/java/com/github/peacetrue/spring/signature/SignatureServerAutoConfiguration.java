package com.github.peacetrue.spring.signature;

import com.github.peacetrue.signature.MemoryNonceVerifier;
import com.github.peacetrue.signature.NonceVerifier;
import com.github.peacetrue.signature.Signer;
import com.github.peacetrue.signature.SignerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static com.github.peacetrue.spring.signature.SignatureAutoConfiguration.SIGNATURE_SIGNER;

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
    @ConditionalOnBean(name = SIGNATURE_SIGNER)
    @ConditionalOnMissingBean(SignerProvider.class)
    public SignerProvider signerProvider(Signer<String, String> signatureSigner) {
        return clientId -> signatureSigner;
    }

    @Bean
    @ConditionalOnMissingBean(NonceVerifier.class)
    public NonceVerifier nonceVerifier() {
        return new MemoryNonceVerifier(properties.getTimestampOffset().getOffset());
    }

    @Bean
    public SignatureServerService signatureVerifyService(SignerProvider signerProvider, NonceVerifier nonceVerifier) {
        return new SignatureServerService(properties.getPropertyNames(), signerProvider, properties.getTimestampOffset(), nonceVerifier);
    }

    @Configuration
    public static class HandlerInterceptorConfiguration {
        @Bean
        public WebMvcConfigurer signatureServerWebMvcConfigurer(
                SignatureServerService signatureVerifyService, SignatureProperties properties
        ) {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    String[] patterns = properties.getVerifyPathPatterns();
                    registry.addInterceptor(new SignatureHandlerInterceptor(signatureVerifyService)).addPathPatterns(patterns);
                }
            };
        }
    }

}
