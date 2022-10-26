package com.github.peacetrue.operator;

import com.github.peacetrue.beans.operator.Operator;
import com.github.peacetrue.beans.operator.OperatorCapable;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 操作者工具类。
 *
 * @author peace
 */
@Slf4j
public abstract class OperatorUtils {

    private OperatorUtils() {
    }

    @SuppressWarnings("unchecked")
    public static void setOperators(Operator<?> operator, OperatorCapable<?> defaultOperator) {
        setOperator((Operator<Serializable>) operator, (OperatorCapable<Serializable>) defaultOperator);
    }

    public static <T extends Serializable> Operator<T> setOperator(Operator<T> operator, OperatorCapable<T> defaultOperator) {
        if (operator.getId() == null) operator.setId(defaultOperator.getId());
        if (operator.getName() == null) operator.setName(defaultOperator.getName());
        return operator;
    }

}
