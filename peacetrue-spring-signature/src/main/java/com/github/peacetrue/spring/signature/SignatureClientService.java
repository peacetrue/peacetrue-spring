package com.github.peacetrue.spring.signature;

import com.github.peacetrue.net.URLCodecUtils;
import com.github.peacetrue.signature.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 生成签名服务。
 *
 * @author peace
 **/
@Slf4j
public class SignatureClientService {

    private final SignatureParameterNames propertyNames;
    @Nullable
    private final String clientId;
    private final SignatureParameterValuesGenerator propertyValuesGenerator;
    private final ClientSecretProvider clientSecretProvider;
    private final StringSignerFactory stringSignerFactory;

    public SignatureClientService(SignatureParameterNames propertyNames,
                                  @Nullable String clientId,
                                  SignatureParameterValuesGenerator propertyValuesGenerator,
                                  ClientSecretProvider clientSecretProvider,
                                  StringSignerFactory stringSignerFactory) {
        this.propertyNames = Objects.requireNonNull(propertyNames);
        this.clientId = clientId;
        this.propertyValuesGenerator = Objects.requireNonNull(propertyValuesGenerator);
        this.clientSecretProvider = Objects.requireNonNull(clientSecretProvider);
        this.stringSignerFactory = Objects.requireNonNull(stringSignerFactory);
    }

    public URI sign(HttpRequest request, byte[] body) {
        log.info("sign request: {}", request.getURI()); // uri encoded

        UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(request).build(true);
        MultiValueMap<String, String> queryParams = decode(uriComponents.getQueryParams());
        log.debug("got query params: {}", queryParams);

        String clientIdName = propertyNames.getClientId();
        if (clientId != null) queryParams.putIfAbsent(clientIdName, Collections.singletonList(clientId));
        String clientIdValue = queryParams.getFirst(clientIdName);
        if (clientIdValue == null) throw new IllegalArgumentException("missing required parameter " + clientIdName);

        Map<String, List<String>> signatureFormParams = propertyValuesGenerator.generateSignatureParameterValues(clientIdValue).toFormParams(propertyNames);
        log.debug("got signature addition params: {}", signatureFormParams);
        signatureFormParams.forEach(queryParams::putIfAbsent);

        String clientSecret = clientSecretProvider.getClientSecret(clientIdValue);
        if (clientSecret == null) throw new ClientInvalidException(clientIdValue);

        String message = SignatureUtils.buildMessage(request.getHeaders().getContentType(), queryParams, body);
        log.debug("build client message: {}", message);

        Signer<String, String> signer = stringSignerFactory.createSigner(clientSecret);
        String signature = signer.sign(message);
        log.debug("generate client signature: {}", signature);
        queryParams.set(propertyNames.getSignature(), signature);
        return UriComponentsBuilder.newInstance()
                .scheme(uriComponents.getScheme())
                .userInfo(uriComponents.getUserInfo())
                .host(uriComponents.getHost())
                .port(uriComponents.getPort())
                .path(uriComponents.getPath())
                .queryParams(queryParams)
                .fragment(uriComponents.getFragment())
                .build().toUri();
    }

    private MultiValueMap<String, String> decode(MultiValueMap<String, String> queryParams) {
        LinkedMultiValueMap<String, String> local = new LinkedMultiValueMap<>(queryParams.size());
        queryParams.forEach((key, values) -> local.put(URLCodecUtils.decode(key), values.stream().map(URLCodecUtils::decode).collect(Collectors.toList())));
        return local;
    }

}
