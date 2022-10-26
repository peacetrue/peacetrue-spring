package com.github.peacetrue.spring.data.relational.core.query;

import org.springframework.data.relational.core.query.Query;

import java.util.function.Supplier;

/**
 * {@link Query} 工具类。
 *
 * @author peace
 **/
public class QueryUtils {

    private QueryUtils() {
    }

    /**
     * 根据主键构造查询对象。
     *
     * @param idSupplier 主键提供者
     * @return 查询对象
     */
    public static Query id(Supplier<?> idSupplier) {
        return Query.query(CriteriaUtils.id(idSupplier));
    }

    /**
     * 根据主键构造查询对象。
     *
     * @param idSupplier 主键提供者
     * @return 查询对象
     */
    public static Query nullableId(Supplier<?> idSupplier) {
        return Query.query(CriteriaUtils.nullableId(idSupplier));
    }

}
