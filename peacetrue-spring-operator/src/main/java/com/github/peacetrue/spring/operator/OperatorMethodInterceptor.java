package com.github.peacetrue.spring.operator;

import com.github.peacetrue.beans.operator.OperatorCapable;
import com.github.peacetrue.operator.OperatorSupplier;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 操作者方法拦截器。
 * <p>
 * 如果方法参数实现了 {@link com.github.peacetrue.beans.properties.operator.Operator} 接口，
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
            if (argument instanceof com.github.peacetrue.beans.properties.operator.Operator) {
                @SuppressWarnings("unchecked")
                com.github.peacetrue.beans.properties.operator.Operator<Object> operatorProperty = (com.github.peacetrue.beans.properties.operator.Operator<Object>) argument;
                if (operatorProperty.getOperator() == null) {
                    OperatorCapable<Object> defaultOperator = operatorSupplier.getOperator();
                    operatorProperty.setOperator(defaultOperator);
                    log.debug("set current Operator: {}", defaultOperator);
                }
            }
        }
        return invocation.proceed();
    }

    /**
     * 设置操作者提供者。
     *
     * @param operatorSupplier 操作者提供者
     */
    public void setOperatorSupplier(@Autowired OperatorSupplier operatorSupplier) {
        this.operatorSupplier = operatorSupplier;
    }
}
