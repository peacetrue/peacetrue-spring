package com.github.peacetrue.spring.data.jpa;

import org.springframework.data.domain.Pageable;

import javax.persistence.Query;

/**
 * 查询工具类。
 *
 * @author peace
 **/
public class QueryUtils {

    private QueryUtils() {
    }

    /**
     * 设置查询数据的起止范围。
     *
     * @param query    查询对象
     * @param pageable 分页对象
     * @param <T>      查询对象类型
     * @return 返回入参 query
     */
    public static <T extends Query> T setPageable(T query, Pageable pageable) {
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return query;
    }
}
