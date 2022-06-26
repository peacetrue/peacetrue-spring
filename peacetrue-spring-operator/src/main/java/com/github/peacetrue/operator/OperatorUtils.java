package com.github.peacetrue.operator;

import com.github.peacetrue.beans.operator.Operator;
import com.github.peacetrue.beans.operator.OperatorCapable;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作者工具类。
 *
 * @author peace
 */
@Slf4j
public class OperatorUtils {

    private OperatorUtils() {
    }

    /**
     * 设置操作者。
     *
     * @param operator        当前操作者
     * @param defaultOperator 默认操作者
     * @param <T>             操作者主键类型
     * @return 当前操作者
     */
    public static <T> Operator<T> setOperator(Operator<T> operator, OperatorCapable<T> defaultOperator) {
        if (operator.getId() == null) operator.setId(defaultOperator.getId());
        if (operator.getName() == null) operator.setName(defaultOperator.getName());
        return operator;
    }

}
