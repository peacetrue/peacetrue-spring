package com.github.peacetrue.spring.signature;

import com.github.peacetrue.signature.SignaturePropertyValues;
import com.github.peacetrue.signature.SignaturePropertyValuesGenerator;
import com.github.peacetrue.signature.Signer;
import com.github.peacetrue.signature.SignerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.Nullable;
import java.util.*;

import static com.github.peacetrue.spring.signature.SignatureAutoConfiguration.SIGNATURE_SIGNER;

/**
 * @author peace
 **/
@Configuration
@ImportAutoConfiguration(classes = {
        SignatureTestController.class,
        HttpMessageConvertersAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class
})
class SignatureTestConfiguration {

    @Bean
    @Autowired(required = false)
    public RestTemplateBuilder restTemplateBuilder(@Nullable List<RestTemplateCustomizer> restTemplateCustomizers) {
        if (restTemplateCustomizers == null) restTemplateCustomizers = Collections.emptyList();
        return new RestTemplateBuilder(restTemplateCustomizers.toArray(new RestTemplateCustomizer[0]));
    }

    @Bean
    @Profile("customSignerProvider")
    @ConditionalOnBean(name = SIGNATURE_SIGNER)
    public SignerProvider signerProvider(@Qualifier(SIGNATURE_SIGNER) Signer<String, String> signatureSigner) {
        Map<String, Signer<String, String>> signers = new HashMap<>();
        return clientId -> signers.computeIfAbsent(clientId, temp -> "errorClientId".equals(temp) ? null : signatureSigner);
    }

    @Bean
    @Profile("customPropertyValuesGenerator")
    public SignaturePropertyValuesGenerator propertyValuesGenerator() {
        return clientId -> {
            SignaturePropertyValues values = new SignaturePropertyValues();
            values.setClientId(clientId);
            values.setTimestamp(System.currentTimeMillis());
            values.setNonce(UUID.randomUUID().toString());
            return values;
        };
    }

}
