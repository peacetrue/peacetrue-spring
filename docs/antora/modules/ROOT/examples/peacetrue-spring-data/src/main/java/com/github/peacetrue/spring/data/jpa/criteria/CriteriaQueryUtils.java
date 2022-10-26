package com.github.peacetrue.spring.data.jpa.criteria;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * 离线查询工具类。
 *
 * @author peace
 **/
public class CriteriaQueryUtils {

    private CriteriaQueryUtils() {
    }

    /**
     * 构造离线查询对象。
     *
     * @param builder     离线查询对象
     * @param domainClass 领域对象类
     * @param where       查询条件
     * @param sort        排序条件
     * @param <T>         领域对象类型
     * @return 离线条件查询
     */
    public static <T> CriteriaQuery<T> buildCriteriaQuery(CriteriaBuilder builder, Class<T> domainClass,
                                                          Specification<T> where, Sort sort) {
        CriteriaQuery<T> query = builder.createQuery(domainClass);
        Root<T> root = query.from(domainClass);
        query.where(where.toPredicate(root, query, builder));
        query.orderBy(QueryUtils.toOrders(sort, root, builder));
        return query;
    }
}
