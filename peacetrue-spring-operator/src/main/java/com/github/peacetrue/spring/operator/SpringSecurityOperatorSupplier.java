package com.github.peacetrue.spring.operator;

import com.github.peacetrue.beans.operator.OperatorCapable;
import com.github.peacetrue.beans.operator.OperatorImpl;
import com.github.peacetrue.beans.properties.id.IdCapable;
import com.github.peacetrue.operator.OperatorSupplier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;

/**
 * 通过 Spring Security 获取当前操作者。
 *
 * @author peace
 **/
public class SpringSecurityOperatorSupplier implements OperatorSupplier {

    /**
     * 如果接口被配置为允许未登陆访问，则此方法无法获取到当前操作者，并返回 {@code null}。
     *
     * @return 当前操作者
     */
    @Nullable
    @Override
    public OperatorCapable<?> getOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof IdCapable)) return null;
        OperatorImpl<Object> operator = new OperatorImpl<>();
        operator.setId(((IdCapable<?>) principal).getId());
        operator.setName(authentication.getName());
        return operator;
    }
}
