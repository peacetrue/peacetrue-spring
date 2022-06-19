package com.github.peacetrue.spring.signature;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 签名验证拦截器。
 *
 * @author peace
 **/
@Slf4j
public class SignatureHandlerInterceptor extends HandlerInterceptorAdapter {

    private final SignatureServerService signatureServerService;

    public SignatureHandlerInterceptor(SignatureServerService signatureServerService) {
        this.signatureServerService = Objects.requireNonNull(signatureServerService);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpServletRequest wrapper = signatureServerService.verify(request);
        if (wrapper != request) {
            String message = "The request can't be modified, Please enable CachedBodyFilterAutoConfiguration firstly";
            throw new UnsupportedOperationException(message);
        }
        return true;
    }

}
