package com.github.peacetrue.spring.data.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peace
 **/
class PageableUtilsTest {

    @Test
    void setSort() {
        Pageable pageable = PageableUtils.PAGEABLE_DEFAULT;
        Sort sort = SortUtils.SORT_ID;
        Pageable pageable2 = PageableUtils.setSort(pageable, sort);
        Assertions.assertEquals(pageable.getPageNumber(), pageable2.getPageNumber());
        Assertions.assertEquals(pageable.getPageSize(), pageable2.getPageSize());
        Assertions.assertEquals(sort, pageable2.getSort());
    }

    private void assertEquals(Pageable pageable, Pageable pageable2) {
        Assertions.assertEquals(pageable.getPageNumber(), pageable2.getPageNumber());
        Assertions.assertEquals(pageable.getPageSize(), pageable2.getPageSize());
        Assertions.assertEquals(pageable.getSort(), pageable2.getSort());
    }

    @Test
    void setDefaultSort() {
        Pageable pageable = PageableUtils.PAGEABLE_DEFAULT;
        Pageable pageable2 = PageableUtils.setDefaultSort(pageable, SortUtils.SORT_ID);
        Assertions.assertNotEquals(pageable, pageable2);
        Pageable pageable3 = PageableUtils.setDefaultSort(pageable2, SortUtils.SORT_CODE);
        assertEquals(pageable2, pageable3);
    }

    @Test
    void setPageSize() {
        Pageable pageable = PageableUtils.PAGEABLE_DEFAULT;
        Pageable pageable1 = PageableUtils.setPageSize(pageable, pageable.getPageSize());
        Assertions.assertEquals(pageable, pageable1);
        int pageSize = pageable.getPageSize() + 1;
        Pageable pageable2 = PageableUtils.setPageSize(pageable, pageSize);
        Assertions.assertEquals(pageable.getPageNumber(), pageable2.getPageNumber());
        Assertions.assertEquals(pageSize, pageable2.getPageSize());
        Assertions.assertEquals(pageable.getSort(), pageable2.getSort());
    }

    @Test
    void setMaxPageSize() {
        Pageable pageable = PageableUtils.PAGEABLE_DEFAULT;
        Pageable pageable1 = PageableUtils.setMaxPageSize(pageable, pageable.getPageSize());
        Assertions.assertEquals(pageable, pageable1);
        int maxPageSize = pageable.getPageSize() - 1;
        Pageable pageable2 = PageableUtils.setMaxPageSize(pageable, maxPageSize);
        Assertions.assertEquals(maxPageSize, pageable2.getPageSize());
    }

    @Test
    void setPageableParams() {
        Pageable pageable = PageableUtils.PAGEABLE_DEFAULT;
        HashMap<String, Object> params = new HashMap<>();
        PageableUtils.setPageableParams(params, pageable);
        Assertions.assertEquals(pageable.getPageNumber(), params.get("page"));
        Assertions.assertEquals(pageable.getPageSize(), params.get("size"));
        Assertions.assertNull(params.get("sort"));
        PageableUtils.setPageableParams(params, PageableUtils.setSort(pageable, SortUtils.SORT_ID));
        Assertions.assertEquals("[id,asc]", params.get("sort").toString());
    }

    @Test
    void buildPageableParams() {
        Pageable pageable = PageableUtils.PAGEABLE_DEFAULT;
        Map<String, Object> params = PageableUtils.buildPageableParams(pageable);
        Assertions.assertEquals(2, params.size());
    }
}
