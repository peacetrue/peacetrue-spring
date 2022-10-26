package com.github.peacetrue.spring.data.domain;

import com.github.peacetrue.beans.properties.code.CodeCapable;
import com.github.peacetrue.beans.properties.createdtime.CreatedTimeCapable;
import com.github.peacetrue.beans.properties.creatorid.CreatorIdCapable;
import com.github.peacetrue.beans.properties.id.IdCapable;
import com.github.peacetrue.beans.properties.modifiedtime.ModifiedTimeCapable;
import com.github.peacetrue.beans.properties.modifierid.ModifierIdCapable;
import com.github.peacetrue.beans.properties.name.NameCapable;
import com.github.peacetrue.beans.properties.serialnumber.SerialNumberCapable;
import com.github.peacetrue.spring.beans.BeanUtils;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排序工具类。
 *
 * @author peace
 **/
public abstract class SortUtils {

    /** 默认排序， 未指定排序，自然顺序 */
    public static final Sort DEFAULT = Sort.unsorted();
    /** 主键升序 */
    public static final Sort SORT_ID = Sort.by(Sort.Order.by(IdCapable.PROPERTY_ID));
    /** 主键降序 */
    public static final Sort SORT_ID_DESC = SORT_ID.descending();
    /** 编码升序 */
    public static final Sort SORT_CODE = Sort.by(Sort.Order.by(CodeCapable.PROPERTY_CODE));
    /** 编码降序 */
    public static final Sort SORT_CODE_DESC = SORT_CODE.descending();
    /** 名称升序 */
    public static final Sort SORT_NAME = Sort.by(Sort.Order.by(NameCapable.PROPERTY_NAME));
    /** 名称降序 */
    public static final Sort SORT_NAME_DESC = SORT_NAME.descending();
    /** 序号升序 */
    public static final Sort SORT_SERIAL_NUMBER = Sort.by(Sort.Order.by(SerialNumberCapable.PROPERTY_SERIAL_NUMBER));
    /** 序号降序 */
    public static final Sort SORT_SERIAL_NUMBER_DESC = SORT_SERIAL_NUMBER.descending();
    /** 创建者升序 */
    public static final Sort SORT_CREATOR_ID = Sort.by(Sort.Order.by(CreatorIdCapable.PROPERTY_CREATOR_ID));
    /** 创建时间升序 */
    public static final Sort SORT_CREATED_TIME = Sort.by(Sort.Order.by(CreatedTimeCapable.PROPERTY_CREATED_TIME));
    /** 创建时间降序 */
    public static final Sort SORT_CREATED_TIME_DESC = SORT_CREATED_TIME.descending();
    /** 修改者升序 */
    public static final Sort SORT_MODIFIER_ID = Sort.by(Sort.Order.by(ModifierIdCapable.PROPERTY_MODIFIER_ID));
    /** 修改时间升序 */
    public static final Sort SORT_MODIFIED_TIME = Sort.by(Sort.Order.by(ModifiedTimeCapable.PROPERTY_MODIFIED_TIME));
    /** 修改时间降序 */
    public static final Sort SORT_MODIFIED_TIME_DESC = SORT_MODIFIED_TIME.descending();

    private SortUtils() {
    }

    /**
     * 获取排序规则比较器，按 Bean 的属性进行比较。
     *
     * @param sort 排序规则
     * @param <T>  排序的 Bean 类型
     * @return 符合排序规则的比较器
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Comparator<T> comparing(Sort sort) {
        return (first, second) -> {
            for (Sort.Order order : sort) {
                Comparable firstValue = (Comparable<?>) BeanUtils.getPropertyValue(first, order.getProperty());
                Comparable secondValue = (Comparable<?>) BeanUtils.getPropertyValue(second, order.getProperty());
                int value = compare(order, firstValue, secondValue);
                if (value != 0) return value;
            }
            return 0;
        };
    }

    private static <T extends Comparable<T>> int compare(Sort.Order order, T first, T second) {
        if (first != null && second != null) {
            return compareNonNull(order, first, second);
        } else {
            return compareNullable(order, first == null, second == null);
        }
    }

    private static <T extends Comparable<T>> int compareNonNull(Sort.Order order, T first, T second) {
        return order.isAscending() ? first.compareTo(second) : second.compareTo(first);
    }

    private static <T extends Comparable<T>> int compareNullable(Sort.Order order, T first, T second) {
        switch (order.getNullHandling()) {
            case NULLS_FIRST:
                return first.compareTo(second);
            case NULLS_LAST:
                return second.compareTo(first);
            case NATIVE:
            default:
                return 0;
        }
    }

    /**
     * 设置排序参数。
     *
     * @param params 请求参数
     * @param sort   排序对象
     */
    public static void setSortParams(Map<String, Object> params, Sort sort) {
        if (sort.isSorted()) params.put("sort", format(sort));
    }

    private static List<String> format(Sort sort) {
        return sort.stream().map(SortUtils::format).collect(Collectors.toList());
    }

    private static String format(Sort.Order order) {
        return order.getProperty() + "," + order.getDirection().name().toLowerCase();
    }
}
