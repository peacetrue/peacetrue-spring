package com.github.peacetrue.spring.operator;

import com.github.peacetrue.beans.operator.OperatorCapable;
import com.github.peacetrue.beans.properties.operator.Operator;
import com.github.peacetrue.beans.properties.operator.OperatorImpl;
import com.github.peacetrue.operator.OperatorSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author peace
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = {OperatorAutoConfiguration.class, UserServiceImpl.class}
)
@ActiveProfiles({"test", "peacetrue-operator"})
class OperatorAutoConfigurationTest {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private OperatorMethodInterceptor operatorMethodInterceptor;

    @Test
    void operatorPointcut() {
        Assertions.assertSame(OperatorSupplier.SYSTEM_SUPPLIER, OperatorAutoConfiguration.getOperatorSupplier(false));

        operatorMethodInterceptor.setOperatorSupplier(OperatorSupplier.SYSTEM_SUPPLIER);
        Operator<?> operator = userService.add(new OperatorImpl<>(), new Object());
        Assertions.assertNotNull(operator.getOperator());

        operatorMethodInterceptor.setOperatorSupplier(new OperatorSupplier() {
            @Override
            public <T> OperatorCapable<T> getOperator() {
                return null;
            }
        });
        operator = userService.add(new OperatorImpl<>(new com.github.peacetrue.beans.operator.OperatorImpl<>()), new Object());
        Assertions.assertNotNull(operator.getOperator());
    }

}
