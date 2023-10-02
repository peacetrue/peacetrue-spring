package com.github.peacetrue.spring.signature;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * 发送请求时添加签名参数。
 *
 * @author peace
 */
@Slf4j
public class SignatureClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final SignatureClientService signatureClientService;

    /**
     * 属性设置实例化。
     *
     * @param signatureClientService 签名客户端服务
     */
    public SignatureClientHttpRequestInterceptor(SignatureClientService signatureClientService) {
        this.signatureClientService = Objects.requireNonNull(signatureClientService);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI uri = signatureClientService.sign(request, body);
        log.debug("got signed request uri: {}", uri);
        return execution.execute(new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                return uri;
            }
        }, body);
    }
}
