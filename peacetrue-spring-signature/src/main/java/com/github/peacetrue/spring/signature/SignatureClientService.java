package com.github.peacetrue.spring.signature;

import com.github.peacetrue.signature.SignaturePropertyNames;
import com.github.peacetrue.signature.SignaturePropertyValues;
import com.github.peacetrue.signature.SignaturePropertyValuesGenerator;
import com.github.peacetrue.signature.Signer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 生成签名服务。
 *
 * @author peace
 **/
@Slf4j
public class SignatureClientService {

    private final SignaturePropertyNames propertyNames;
    private final Supplier<SignaturePropertyValues> propertyValues;
    private final Signer<String, String> signatureSigner;

    public SignatureClientService(SignaturePropertyNames propertyNames,
                                  Supplier<SignaturePropertyValues> propertyValues,
                                  Signer<String, String> signatureSigner) {
        this.propertyNames = Objects.requireNonNull(propertyNames);
        this.propertyValues = Objects.requireNonNull(propertyValues);
        this.signatureSigner = Objects.requireNonNull(signatureSigner);
    }

    public URI sign(HttpRequest request, byte[] body) {
        log.info("sign request: {}", request.getURI());

        UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(request).build();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(uriComponents.getQueryParams());//Collections$UnmodifiableMap
        log.debug("got query params: {}", queryParams);

        Map<String, List<String>> signatureFormParams = propertyValues.get().toFormParams(propertyNames);
        log.debug("got signature addition params: {}", signatureFormParams);
        signatureFormParams.forEach(queryParams::putIfAbsent);

        String message = SignatureUtils.buildMessage(request.getHeaders().getContentType(), queryParams, body);
        log.debug("build client message: {}", message);

        String signature = signatureSigner.sign(message);
        log.debug("generate client signature: {}", signature);
        queryParams.put(propertyNames.getSignature(), Collections.singletonList(signature));

        return UriComponentsBuilder.fromHttpRequest(request).queryParams(queryParams).build().toUri();
    }

}
