package com.github.peacetrue.operator;

import com.github.peacetrue.beans.operator.Operator;
import com.github.peacetrue.beans.operator.OperatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author peace
 **/
class OperatorTest {

    @Test
    void setOperator() {
        OperatorImpl<Object> source = new OperatorImpl<>();
        OperatorImpl<Object> defaults = new OperatorImpl<>();
        Operator.setDefault(source, defaults);
        Assertions.assertNull(source.getId());
        Assertions.assertNull(source.getName());

        defaults.setId(1L);
        defaults.setName("admin");
        Operator.setDefault(source, defaults);
        Assertions.assertEquals(source.getId(), defaults.getId());
        Assertions.assertEquals(source.getName(), defaults.getName());

        source.setId(2L);
        Operator.setDefault(source, defaults);
        Assertions.assertNotEquals(source.getId(), defaults.getId());
        Assertions.assertEquals(source.getName(), defaults.getName());

        source.setName("admin2");
        Operator.setDefault(source, defaults);
        Assertions.assertNotEquals(source.getId(), defaults.getId());
        Assertions.assertNotEquals(source.getName(), defaults.getName());
    }
}
