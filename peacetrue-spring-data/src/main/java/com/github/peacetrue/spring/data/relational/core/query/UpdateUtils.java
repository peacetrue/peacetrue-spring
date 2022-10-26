package com.github.peacetrue.spring.data.relational.core.query;

import com.github.peacetrue.beans.properties.id.IdCapable;
import com.github.peacetrue.spring.beans.BeanUtils;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * {@link Update} 工具类。
 *
 * @author peace
 **/
public class UpdateUtils {

    private UpdateUtils() {
    }

    /**
     * 选着性更新，忽略空值和主键。
     *
     * @param example 更新示例
     * @return 更新对象
     */
    public static Update selectiveUpdateFromExample(Object example) {
        BiPredicate<String, Object> id = (key, value) -> !key.equals(IdCapable.PROPERTY_ID);
        return selectiveUpdateFromExample(example, id.and((key, value) -> !ObjectUtils.isEmpty(value)));
    }

    /**
     * 选着性更新。
     *
     * @param example         更新示例
     * @param includeProperty 包含哪些属性
     * @return 更新对象
     */
    public static Update selectiveUpdateFromExample(Object example, BiPredicate<String, Object> includeProperty) {
        return Update.from(
                BeanUtils.getPropertyValues(example).entrySet().stream()
                        .filter(entry -> includeProperty.test(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toMap(entry -> SqlIdentifier.unquoted(entry.getKey()), Map.Entry::getValue))
        );
    }
}
