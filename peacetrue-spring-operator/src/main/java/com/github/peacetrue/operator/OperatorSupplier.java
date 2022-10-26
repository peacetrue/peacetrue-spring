package com.github.peacetrue.operator;

import com.github.peacetrue.beans.operator.OperatorCapable;

/**
 * 获取当前操作者。
 *
 * @author peace
 **/
@FunctionalInterface
public interface OperatorSupplier {

    /** 系统操作者，找不到操作者时使用的默认操作者。 */
    OperatorCapable<Long> SYSTEM = new OperatorCapable<Long>() {
        @Override
        public Long getId() {
            return 0L;
        }

        @Override
        public String getName() {
            return "system";
        }
    };

    /** 系统操作者提供者。 */
    OperatorSupplier SYSTEM_SUPPLIER = new OperatorSupplier() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> OperatorCapable<T> getOperator() {
            return (OperatorCapable<T>) SYSTEM;
        }
    };

    /**
     * 获取当前操作者。
     *
     * @param <T> 操作者主键类型
     * @return 当前操作者。如果当前操作与用户无关，则返回系统
     */
    <T> OperatorCapable<T> getOperator();

}
