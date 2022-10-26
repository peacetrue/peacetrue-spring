package com.github.peacetrue.operator;

import com.github.peacetrue.beans.operator.OperatorCapable;
import com.github.peacetrue.beans.operator.OperatorImpl;

import javax.annotation.Nullable;

/**
 * 获取当前操作者。
 *
 * @author peace
 **/
@FunctionalInterface
public interface OperatorSupplier {

    /** 操作者为当前系统，找不到操作时使用的默认操作者。 */
    OperatorSupplier SYSTEM = () -> new OperatorImpl<>(0L, "system");

    /**
     * 获取当前操作者。
     *
     * @return 当前操作者。如果当前操作与用户无关，则返回 {@code null}
     */
    @Nullable
    OperatorCapable<?> getOperator();

}
