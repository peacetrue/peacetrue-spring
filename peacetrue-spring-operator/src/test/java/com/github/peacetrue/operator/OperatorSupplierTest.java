package com.github.peacetrue.operator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author peace
 **/
class OperatorSupplierTest {

    @Test
    void getOperator() {
        Assertions.assertNotNull(OperatorSupplier.SYSTEM_SUPPLIER.getOperator());
    }
}
