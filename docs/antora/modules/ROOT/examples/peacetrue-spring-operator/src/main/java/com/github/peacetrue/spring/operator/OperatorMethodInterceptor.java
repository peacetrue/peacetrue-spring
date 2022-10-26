package com.github.peacetrue.spring.operator;

import com.github.peacetrue.beans.operator.Operator;
import com.github.peacetrue.beans.operator.OperatorCapable;
import com.github.peacetrue.beans.properties.context.Context;
import com.github.peacetrue.beans.properties.context.ContextCapable;
import com.github.peacetrue.operator.OperatorSupplier;
import com.github.peacetrue.operator.OperatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 操作者方法拦截器。
 * <p>
 * 如果方法参数实现了 {@link ContextCapable} 接口
 * 且 {@link ContextCapable#getContext()} 的返回值实现了 {@link Operator} 接口，
 * 则设置当前操作者。
 *
 * @author peace
 **/
@Slf4j
public class OperatorMethodInterceptor implements MethodInterceptor {

    private OperatorSupplier operatorSupplier;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        for (Object argument : arguments) {
            if (argument instanceof ContextCapable) {
                Context<?> contextCapable = (Context<?>) argument;
                Object context = contextCapable.getContext();
                //TODO 重复设置
                if (context instanceof Operator) {
                    OperatorCapable<?> defaultOperator = operatorSupplier.getOperator();
                    log.debug("set current Operator: {}", defaultOperator);
                    OperatorUtils.setOperators((Operator<?>) context, defaultOperator);
                }
            }
        }
        return invocation.proceed();
    }

    @Autowired
    public void setOperatorSupplier(OperatorSupplier operatorSupplier) {
        this.operatorSupplier = operatorSupplier;
    }
}
