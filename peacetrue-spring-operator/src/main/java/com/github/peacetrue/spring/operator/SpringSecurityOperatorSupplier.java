package com.github.peacetrue.spring.operator;

import com.github.peacetrue.beans.operator.OperatorCapable;
import com.github.peacetrue.beans.operator.OperatorImpl;
import com.github.peacetrue.beans.properties.id.IdCapable;
import com.github.peacetrue.operator.OperatorSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * 通过 Spring Security 获取当前操作者。
 *
 * @author peace
 **/
@Slf4j
public class SpringSecurityOperatorSupplier implements OperatorSupplier {

    private final OperatorCapable<?> fallbackOperator;

    public SpringSecurityOperatorSupplier(OperatorCapable<?> fallbackOperator) {
        this.fallbackOperator = Objects.requireNonNull(fallbackOperator);
    }

    /**
     * 如果接口被配置为允许未登陆访问，则此方法无法获取到当前操作者，并返回 {@code null}。
     *
     * @return 当前操作者
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> OperatorCapable<T> getOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.trace("got authentication: {}", authentication);
        if (authentication == null) return (OperatorCapable<T>) fallbackOperator;
        Object principal = authentication.getPrincipal();
        log.trace("got principal: {}", principal);
        if (!(principal instanceof IdCapable)) {
            log.warn("principal {} is not a IdCapable", principal);
            return (OperatorCapable<T>) fallbackOperator;
        }
        OperatorImpl<T> operator = new OperatorImpl<>();
        operator.setId(((IdCapable<T>) principal).getId());
        operator.setName(authentication.getName());
        return operator;
    }
}
