package com.github.peacetrue.spring.signature;

import com.github.peacetrue.codec.Codec;
import com.github.peacetrue.signature.*;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.peacetrue.spring.signature.SignatureAutoConfiguration.CLIENT_SECRET_CODEC;

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
    @Profile("customStringSignerFactory")
    public StringSignerFactory stringSignerFactory(@Qualifier(CLIENT_SECRET_CODEC) Codec clientSecretCodec) {
        return new CodecSignerFactory(BytesSignerFactory.HmacSHA256, clientSecretCodec);
    }

    @Bean
    @Profile("customClientSecretProvider")
    public ClientSecretProvider clientSecretProvider() {
        AtomicInteger serverMissing = new AtomicInteger(0);
        return clientId -> {
            if ("random".equals(clientId)) return RandomStringUtils.randomNumeric(10);
            if ("fix".equals(clientId)) return "11";
            if ("serverMissing".equals(clientId) && serverMissing.getAndIncrement() % 2 == 0) return "11";
            return null;
        };
    }

    @Bean
    @Profile("customPropertyValuesGenerator")
    public SignatureParameterValuesGenerator propertyValuesGenerator() {
        return clientId -> {
            SignatureParameterValues values = new SignatureParameterValues();
            values.setTimestamp(System.currentTimeMillis());
            values.setNonce(UUID.randomUUID().toString());
            return values;
        };
    }

}
