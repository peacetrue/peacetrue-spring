package com.github.peacetrue.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 继承 {@link LinkedHashMap} 实现 {@link BeanMap}
 *
 * @author peace
 * @since 1.0
 **/
public class BeanMapImpl extends LinkedHashMap<String, Object> implements BeanMap {

    public BeanMapImpl(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public BeanMapImpl(int initialCapacity) {
        super(initialCapacity);
    }

    public BeanMapImpl() {
    }

    public BeanMapImpl(Map<? extends String, ?> m) {
        super(m);
    }

    public BeanMapImpl(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }
}
