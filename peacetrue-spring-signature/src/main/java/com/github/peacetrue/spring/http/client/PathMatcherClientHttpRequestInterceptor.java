package com.github.peacetrue.spring.http.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 支持路径匹配。
 *
 * @author peace
 **/
@Slf4j
public class PathMatcherClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final ClientHttpRequestInterceptor interceptor;
    private final PathMatcher pathMatcher;
    private final List<String> pathPatterns;

    /**
     * 实例化。
     *
     * @param interceptor  拦截器
     * @param pathPatterns 路径规则集合
     */
    public PathMatcherClientHttpRequestInterceptor(ClientHttpRequestInterceptor interceptor, List<String> pathPatterns) {
        this(new AntPathMatcher(), interceptor, pathPatterns);
    }

    /**
     * 实例化。
     *
     * @param pathMatcher  路径匹配器
     * @param interceptor  拦截器
     * @param pathPatterns 路径规则集合
     */
    public PathMatcherClientHttpRequestInterceptor(PathMatcher pathMatcher, ClientHttpRequestInterceptor interceptor, List<String> pathPatterns) {
        this.pathMatcher = Objects.requireNonNull(pathMatcher);
        this.interceptor = Objects.requireNonNull(interceptor);
        this.pathPatterns = Objects.requireNonNull(pathPatterns);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        boolean matched = match(request);
        log.debug("is matched request: {}", matched);
        return matched
                ? interceptor.intercept(request, body, execution)
                : execution.execute(request, body);
    }

    /**
     * 是否匹配该请求。
     *
     * @param request 请求
     * @return true 如果匹配，否则 false
     */
    protected boolean match(HttpRequest request) {
        return match(request.getURI().getPath());
    }

    /**
     * 是否匹配该请求 uri。
     *
     * @param uriPath 请求 uri
     * @return true 如果匹配，否则 false
     */
    protected boolean match(String uriPath) {
        log.debug("try to match request uri path: {}", uriPath);
        return pathPatterns.stream().anyMatch(item -> trace("is matched by {}: {}", item, pathMatcher.match(item, uriPath)));
    }

    private static <T> T trace(String message, Object... args) {
        return trace(message, args.length - 1, args);
    }

    @SuppressWarnings("unchecked")
    private static <T> T trace(String message, int index, Object... args) {
        log.trace(message, args);
        return (T) args[index];
    }
}
