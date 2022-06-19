package com.github.peacetrue.spring.signature;

import com.github.peacetrue.spring.signature.SignatureServerService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * 签名验证拦截器。
 *
 * @author peace
 **/
@Slf4j
public class SignatureServerFilter implements Filter {

    private final SignatureServerService signatureVerifyService;

    public SignatureServerFilter(SignatureServerService signatureVerifyService) {
        this.signatureVerifyService = Objects.requireNonNull(signatureVerifyService);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            request = signatureVerifyService.verify((HttpServletRequest) request);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }


}
