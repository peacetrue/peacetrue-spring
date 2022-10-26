package com.github.peacetrue.spring.operator;

import com.github.peacetrue.beans.properties.id.IdCapable;
import com.github.peacetrue.operator.OperatorSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author peace
 **/
class SpringSecurityOperatorSupplierTest {

    @Test
    void getOperator() {
        SpringSecurityOperatorSupplier operatorSupplier = new SpringSecurityOperatorSupplier(OperatorSupplier.SYSTEM);
        Assertions.assertEquals(OperatorSupplier.SYSTEM, operatorSupplier.getOperator());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new Object(), new Object());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assertions.assertEquals(OperatorSupplier.SYSTEM, operatorSupplier.getOperator());

        authentication = new UsernamePasswordAuthenticationToken((IdCapable<Long>) () -> 1L, new Object());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assertions.assertNotNull(operatorSupplier.getOperator());
    }
}
