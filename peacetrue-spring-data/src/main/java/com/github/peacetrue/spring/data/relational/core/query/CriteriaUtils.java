package com.github.peacetrue.spring.data.relational.core.query;

import com.github.peacetrue.beans.properties.id.IdCapable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * {@link Criteria} 工具类。
 *
 * @author peace
 **/
public class CriteriaUtils {

    private CriteriaUtils() {
    }

    /**
     * 构造并且条件。
     *
     * @param criteria 条件集合
     * @return 条件
     */
    public static Criteria and(@Nullable Criteria... criteria) {
        return logicCalculate(CriteriaDefinition.Combinator.AND, criteria);
    }

    /**
     * 构造或者条件。
     *
     * @param criteria 条件集合
     * @return 条件
     */
    public static Criteria or(@Nullable Criteria... criteria) {
        return logicCalculate(CriteriaDefinition.Combinator.OR, criteria);
    }

    /**
     * 逻辑运算条件。
     *
     * @param combinator 逻辑操作
     * @param criteria   条件集合
     * @return 条件
     */
    public static Criteria logicCalculate(CriteriaDefinition.Combinator combinator, @Nullable Criteria... criteria) {
        Criteria empty = Criteria.empty();
        if (criteria == null || criteria.length == 0) return empty;
        if (criteria.length == 1) return nullToEmpty(criteria[0]);
        Criteria returnCriteria = null;
        for (Criteria itemCriteria : criteria) {
            if (itemCriteria == null || itemCriteria == empty) continue;
            if (returnCriteria == null) returnCriteria = itemCriteria;
            else {
                switch (combinator) {
                    case AND:
                        returnCriteria = returnCriteria.and(itemCriteria);
                        break;
                    case OR:
                        returnCriteria = returnCriteria.or(itemCriteria);
                        break;
                    default:
                        throw new IllegalArgumentException("combinator must be AND or OR");
                }
            }
        }
        return nullToEmpty(returnCriteria);
    }

    /**
     * {@code null} 条件转空条件，防止空指针异常。
     *
     * @param criteria 条件
     * @return 条件
     */
    public static Criteria nullToEmpty(@Nullable Criteria criteria) {
        return Optional.ofNullable(criteria).orElse(Criteria.empty());
    }

    /**
     * 构造可空的条件。
     *
     * @param criteriaConverter 条件转换器
     * @param valueSupplier     值提供者
     * @param <T>               值类型
     * @return 条件
     */
    public static <T> Criteria nullableCriteria(Function<T, Criteria> criteriaConverter, Supplier<T> valueSupplier) {
        return innerNullableCriteria(criteriaConverter, valueSupplier.get());
    }

    /**
     * 构造可空的条件。
     *
     * @param criteriaConverter 条件转换器
     * @param valueConverter    值转换器
     * @param valueSupplier     值提供者
     * @param <T>               值类型
     * @return 条件
     */
    public static <T> Criteria nullableCriteria(Function<T, Criteria> criteriaConverter, UnaryOperator<T> valueConverter, Supplier<T> valueSupplier) {
        return innerNullableCriteria(criteriaConverter, valueConverter, valueSupplier.get());
    }

    private static <T> Criteria innerNullableCriteria(Function<T, Criteria> criteriaConverter, T value) {
        return innerNullableCriteria(criteriaConverter, UnaryOperator.identity(), value);
    }

    private static <T> Criteria innerNullableCriteria(Function<T, Criteria> criteriaConverter, UnaryOperator<T> valueConverter, T valueSupplier) {
        return valueSupplier == null ? Criteria.empty() : criteriaConverter.apply(valueConverter.apply(valueSupplier));
    }

    /**
     * 根据主键构造条件。
     *
     * @param idSupplier 主键提供者
     * @return 条件
     */
    public static Criteria nullableId(Supplier<?> idSupplier) {
        return nullableCriteria(Criteria.where(IdCapable.PROPERTY_ID)::is, idSupplier);
    }

    /**
     * 根据主键构造条件。
     *
     * @param id 主键
     * @return 条件
     */
    public static Criteria id(Object id) {
        return Criteria.where(IdCapable.PROPERTY_ID).is(id);
    }

    /**
     * 根据主键构造条件。
     *
     * @param idSupplier 主键提供者
     * @return 条件
     */
    public static Criteria id(Supplier<?> idSupplier) {
        return Criteria.where(IdCapable.PROPERTY_ID).is(idSupplier.get());
    }

    /**
     * 构造智能 in 条件。
     *
     * @param column 列名
     * @param value  列值
     * @return 条件
     */
    public static Criteria smartIn(String column, Object... value) {
        return smartIn(column).apply(value);
    }

    /**
     * 构造智能 in 条件。
     *
     * @param column 列名
     * @return 条件
     */
    public static Function<Object[], Criteria> smartIn(String column) {
        return array -> {
            Criteria.CriteriaStep columnStep = Criteria.where(column);
            return array.length == 1 ? columnStep.is(array[0]) : columnStep.in(array);
        };
    }

}
