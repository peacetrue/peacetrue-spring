package com.github.peacetrue.spring.data.domain;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页工具类。
 *
 * @author peace
 **/
public abstract class PageableUtils {

    /** 默认分页对象 */
    public static final Pageable PAGEABLE_DEFAULT = PageRequest.of(0, 10);

    private PageableUtils() {
    }

    /**
     * 设置排序对象。
     *
     * @param pageable 分页对象
     * @param sort     排序对象
     * @return 设置排序对象后的分页对象。
     */
    public static Pageable setSort(Pageable pageable, Sort sort) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    /**
     * 设置默认排序对象。如果排序对象为空，设置一个默认排序对象。
     *
     * @param pageable    分页对象
     * @param defaultSort 排序对象
     * @return 包含有效排序对象的分页对象
     */
    public static Pageable setDefaultSort(Pageable pageable, Sort defaultSort) {
        return pageable.getSort().isSorted() ? pageable : setSort(pageable, defaultSort);
    }

    /**
     * 设置每页记录数。
     *
     * @param pageable 分页对象
     * @param pageSize 每页记录数
     * @return 分页对象
     */
    public static Pageable setPageSize(Pageable pageable, int pageSize) {
        return pageable.getPageSize() == pageSize
                ? pageable : PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());
    }

    /**
     * 设置最大每页记录数。
     * 如果当前每页记录数超过最大每页记录数，更新为最大每页记录数。
     *
     * @param pageable    分页对象
     * @param maxPageSize 最大每页记录数
     * @return 分页对象
     */
    public static Pageable setMaxPageSize(Pageable pageable, int maxPageSize) {
        return pageable.getPageSize() <= maxPageSize ? pageable : setPageSize(pageable, maxPageSize);
    }

    /**
     * 构建分页参数。
     *
     * @param pageable 分页对象
     * @return 分页参数
     */
    public static Map<String, Object> buildPageableParams(Pageable pageable) {
        Map<String, Object> params = new HashMap<>(3);
        setPageableParams(params, pageable);
        return params;
    }

    /**
     * 设置分页参数。
     *
     * @param params   请求参数
     * @param pageable 分页对象
     */
    public static void setPageableParams(Map<String, Object> params, Pageable pageable) {
        params.put("page", pageable.getPageNumber());
        params.put("size", pageable.getPageSize());
        SortUtils.setSortParams(params, pageable.getSort());
    }

}
