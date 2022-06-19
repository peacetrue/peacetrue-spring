package com.github.peacetrue.spring.signature;

import com.github.peacetrue.codec.Codec;
import com.github.peacetrue.digest.HmacDigester;
import com.github.peacetrue.security.KeyFactoryUtils;
import com.github.peacetrue.signature.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 签名自动配置。
 *
 * @author peace
 **/
@Configuration
@EnableConfigurationProperties(SignatureProperties.class)
public class SignatureAutoConfiguration {

    public static final String SIGNATURE_CODEC = "signatureCodec";
    public static final String SIGNATURE_SIGNER = "signatureSigner";

    private final SignatureProperties properties;

    public SignatureAutoConfiguration(SignatureProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SignaturePropertyNames signaturePropertyNames() {
        return properties.getPropertyNames();
    }

    @Bean
    @ConditionalOnMissingBean(name = SIGNATURE_CODEC)
    public Codec signatureCodec() {
        return Codec.HEX;
    }

    @Bean(name = SIGNATURE_SIGNER)
    @ConditionalOnMissingBean(name = SIGNATURE_SIGNER)
    @ConditionalOnExpression("'${peacetrue.signature.secret-key:}'!='' or '${peacetrue.signature.private-key:}'!='' or '${peacetrue.signature.public-key:}'!=''")
    public Signer<String, String> signatureSigner(@Qualifier(SIGNATURE_CODEC) Codec signatureCodec) {
        Signer<byte[], byte[]> signer = properties.getSecretKey() == null
                ? getAsymmetricSigner(signatureCodec, properties.getPrivateKey(), properties.getPublicKey())
                : getDigestSigner(signatureCodec, properties.getSecretKey());
        return new CodecSigner(signer);
    }

    private static AsymmetricSigner getAsymmetricSigner(Codec keyCodec, @Nullable String privateKey, @Nullable String publicKey) {
        return new AsymmetricSigner(
                Optional.ofNullable(privateKey)
                        .map(keyCodec::decode)
                        .map(KeyFactoryUtils::generateRsaPrivate)
                        .orElse(null),
                Optional.ofNullable(publicKey)
                        .map(keyCodec::decode)
                        .map(KeyFactoryUtils::generateRsaPublic)
                        .orElse(null)
        );
    }

    private static Signer<byte[], byte[]> getDigestSigner(Codec keyCodec, String secretKey) {
        return new DigestSigner(HmacDigester.buildHmacSHA256(keyCodec.decode(secretKey)));
    }

}
