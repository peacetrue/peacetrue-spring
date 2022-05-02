package com.github.peacetrue.spring.beans;

import com.github.peacetrue.util.function.PredicateUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.peacetrue.util.function.PredicateUtils.headConvert;
import static com.github.peacetrue.util.function.PredicateUtils.negate;

/**
 * 扩展 {@link org.springframework.beans.BeanUtils}
 *
 * @author peace
 */
@SuppressWarnings("java:S2176")
public abstract class BeanUtils extends org.springframework.beans.BeanUtils {

    /** 抽象工具类，无需实例化 */
    protected BeanUtils() {
    }

    /**
     * 排序属性。使用 {@link Order} 标记属性，支持标记在字段和方法上。
     *
     * @param properties 属性集合
     */
    public static void sort(PropertyDescriptor... properties) {
        Arrays.sort(properties, Comparator.comparing(BeanUtils::getOrder));
    }

    private static int getOrder(PropertyDescriptor property) {
        Optional<Integer> order = findMethod(property)
                .map(method -> method.getAnnotation(Order.class))
                .filter(Objects::nonNull)
                .map(Order::value)
                .findAny();
        return order.orElseGet(() -> findField(property)
                .map(field -> field.getAnnotation(Order.class))
                .map(Order::value)
                .orElse(0));
    }

    private static Stream<Method> findMethod(PropertyDescriptor property) {
        return Stream.of(property.getReadMethod(), property.getWriteMethod())
                .filter(Objects::nonNull);
    }

    private static Optional<Field> findField(PropertyDescriptor property) {
        return findMethod(property)
                .findAny()
                .map(Method::getDeclaringClass)
                .map(declaringClass -> ReflectionUtils.findField(declaringClass, property.getName()));
    }

    /**
     * 获取指定对象的属性值，如果属性不存在，返回 {@code null}
     *
     * @param bean         指定对象
     * @param propertyName 属性名
     * @return 属性值
     */
    @Nullable
    public static Object getPropertyValue(Object bean, String propertyName) {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd == null) return null;
        Method readMethod = pd.getReadMethod();
        if (readMethod == null) return null;
        return ReflectionUtils.invokeMethod(readMethod, bean);
    }

    /**
     * 获取指定对象的属性值
     *
     * @param bean     指定对象
     * @param property 属性
     * @return 属性值
     */
    @Nullable
    private static Object getPropertyValue(Object bean, PropertyDescriptor property) {
        return Optional.ofNullable(property.getReadMethod())
                .map(item -> ReflectionUtils.invokeMethod(item, bean))
                .orElse(null);
    }

    /**
     * 获取指定对象的所有属性值，不排序属性
     *
     * @param bean 指定对象
     * @return 属性值
     */
    public static Map<String, Object> getPropertyValues(Object bean) {
        return getPropertyValues(bean, false, false);
    }

    /**
     * 获取指定对象的所有属性值
     *
     * @param bean         指定对象
     * @param sortProperty 是否排序属性。如果排序，需要通过 {@link Order} 指定顺序
     * @param ignoreNull   忽略 {@code null} 值
     * @return 属性值
     */
    public static Map<String, Object> getPropertyValues(Object bean, boolean sortProperty, boolean ignoreNull) {
        Map<String, Object> propertyValues = new LinkedHashMap<>();
        PropertyDescriptor[] properties = getPropertyDescriptors(bean.getClass());
        if (sortProperty) sort(properties);
        foreachProperties(properties, property -> {
            Object propertyValue = getPropertyValue(bean, property);
            if (ignoreNull && propertyValue == null) return;
            propertyValues.put(property.getName(), propertyValue);
        });
        return propertyValues;
    }

    /**
     * 获取指定对象的属性值集合
     *
     * @param beans        指定对象集合
     * @param propertyName 属性名
     * @param <T>          属性值类型
     * @return 属性值集合
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getPropertyValues(Collection<?> beans, String propertyName) {
        return beans.stream().map(bean -> (T) getPropertyValue(bean, propertyName)).collect(Collectors.toList());
    }

    /**
     * 获取指定对象集合的属性值集合
     *
     * @param beans 指定对象集合
     * @return 属性值集合
     */
    public static List<Map<String, Object>> getPropertyValues(Collection<?> beans) {
        return beans.stream().map(BeanUtils::getPropertyValues).collect(Collectors.toList());
    }

    /**
     * 设置指定对象的属性值。如果属性不存在，直接返回
     *
     * @param bean          指定对象
     * @param propertyName  属性名
     * @param propertyValue 属性值
     */
    public static void setPropertyValue(Object bean, String propertyName, @Nullable Object propertyValue) {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd == null) return;
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null) return;
        ReflectionUtils.invokeMethod(writeMethod, bean, propertyValue);
    }

    private static void setPropertyValue(Object source, PropertyDescriptor sourcePd,
                                         Object target, PropertyDescriptor targetPd) {
        ReflectionUtils.invokeMethod(targetPd.getWriteMethod(), target, getPropertyValue(source, sourcePd));
    }

    /**
     * 遍历指定对象的所有属性
     *
     * @param bean            指定对象
     * @param propertyHandler 属性处理器
     */
    public static void foreachProperties(Object bean, Consumer<PropertyDescriptor> propertyHandler) {
        foreachProperties(getPropertyDescriptors(bean.getClass()), propertyHandler);
    }

    private static void foreachProperties(PropertyDescriptor[] properties,
                                          Consumer<PropertyDescriptor> propertyHandler) {
        for (PropertyDescriptor descriptor : properties) {
            if ("class".equals(descriptor.getName())) continue;
            Method readMethod = descriptor.getReadMethod();
            if (readMethod != null) propertyHandler.accept(descriptor);
        }
    }

    /**
     * 遍历目标对象的所有属性，同时查找对应的原始属性
     *
     * @param source          原始对象
     * @param target          目标对象
     * @param propertyHandler 属性处理器。参数1为原始对象的属性，参数2为目标对象的属性，两者属性相同
     */
    @SuppressWarnings("java:S135")
    public static void foreachProperties(Object source, Object target,
                                         BiConsumer<PropertyDescriptor, PropertyDescriptor> propertyHandler) {
        PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());
        for (PropertyDescriptor targetPd : targetPds) {
            if ("class".equals(targetPd.getName())) continue;
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod == null) continue;
            PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
            if (sourcePd == null) continue;
            Method readMethod = sourcePd.getReadMethod();
            if (readMethod == null) continue;
            if (!ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) continue;
            propertyHandler.accept(sourcePd, targetPd);
        }
    }

    /**
     * 拷贝原始对象中符合条件的属性到目标对象
     *
     * @param source               原始对象
     * @param target               目标对象
     * @param sourcePropertyFilter 原始对象属性和属性值过滤器，参数1为原始对象的属性，参数2为原始对象的属性值
     */
    private static void copyProperties(Object source, Object target,
                                       BiPredicate<PropertyDescriptor, Supplier<Object>> sourcePropertyFilter) {
        foreachProperties(source, target, (sourcePd, targetPd) -> {
            boolean matched = sourcePropertyFilter.test(sourcePd, () -> getPropertyValue(source, sourcePd));
            if (matched) setPropertyValue(source, sourcePd, target, targetPd);
        });
    }

    /**
     * 拷贝原始对象中符合条件的属性到目标对象
     *
     * @param source                    原始对象
     * @param target                    目标对象
     * @param sourcePropertyFilter      原始对象属性过滤器
     * @param sourcePropertyValueFilter 原始对象属性值过滤器
     */
    public static void copyProperties(Object source, Object target,
                                      @Nullable Predicate<PropertyDescriptor> sourcePropertyFilter,
                                      @Nullable Predicate<Object> sourcePropertyValueFilter) {
        BiPredicate<PropertyDescriptor, Supplier<Object>> predicate = PredicateUtils::alwaysTrue;
        if (sourcePropertyFilter != null) predicate = predicate.and(headConvert(sourcePropertyFilter));
        if (sourcePropertyValueFilter != null) {
            predicate = predicate.and((pd, vs) -> sourcePropertyValueFilter.test(vs.get()));
        }
        BeanUtils.copyProperties(source, target, predicate);
    }

    /**
     * 拷贝原始对象中所有属性到目标对象
     *
     * @param source 原始对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, PredicateUtils::alwaysTrue);
    }

    private static Predicate<PropertyDescriptor> containsProperty(Collection<String> propertyNames) {
        return pd -> propertyNames.contains(pd.getName());
    }

    /**
     * 拷贝原始对象中指定的属性到目标对象
     *
     * @param source        原始对象
     * @param target        目标对象
     * @param propertyNames 包含的属性集合
     */
    public static void copyPropertiesInclude(Object source, Object target, Collection<String> propertyNames) {
        BeanUtils.copyProperties(
                source, target, containsProperty(propertyNames), null
        );
    }

    /**
     * 拷贝原始对象中未指定的属性到目标对象
     *
     * @param source        原始对象
     * @param target        目标对象
     * @param propertyNames 排除的属性集合
     */
    public static void copyPropertiesExclude(Object source, Object target, Collection<String> propertyNames) {
        BeanUtils.copyProperties(
                source, target, containsProperty(propertyNames).negate(), null
        );
    }

    /**
     * 拷贝原始对象中非空属性到目标对象
     *
     * @param source 原始对象
     * @param target 目标对象
     */
    public static void copyPropertiesNotEmpty(Object source, Object target) {
        BeanUtils.copyProperties(
                source, target, PredicateUtils::alwaysTrue, negate(ObjectUtils::isEmpty)
        );
    }

    /**
     * 拷贝原始对象中指定的非空属性到目标对象
     *
     * @param source        原始对象
     * @param target        目标对象
     * @param propertyNames 原始对象的属性名集合
     */
    public static void copyPropertiesNotEmptyInclude(Object source, Object target, Collection<String> propertyNames) {
        BeanUtils.copyProperties(
                source, target, containsProperty(propertyNames), negate(ObjectUtils::isEmpty)
        );
    }

    /**
     * 拷贝原始对象中未指定的非空属性到目标对象
     *
     * @param source        原始对象
     * @param target        目标对象
     * @param propertyNames 排除的属性集合
     */
    public static void copyPropertiesNotEmptyExclude(Object source, Object target, Collection<String> propertyNames) {
        BeanUtils.copyProperties(
                source, target, containsProperty(propertyNames).negate(), negate(ObjectUtils::isEmpty)
        );
    }

    /**
     * 将默认对象中非空属性值设置到原始对象的空属性上。
     *
     * @param source   原始对象
     * @param defaults 默认对象
     */
    public static void setDefaults(Object source, Object defaults) {
        foreachProperties(source, defaults, (sourceProperty, defaultsProperty) -> {
            Object sourceValue = getPropertyValue(source, sourceProperty);
            if (!ObjectUtils.isEmpty(sourceValue)) return;
            Object defaultsValue = getPropertyValue(defaults, defaultsProperty);
            if (ObjectUtils.isEmpty(defaultsValue)) return;
            ReflectionUtils.invokeMethod(sourceProperty.getWriteMethod(), source, defaultsValue);
        });
    }

    /**
     * 转换原始对象到目标类型的对象
     * <p>
     * 使用场景：DTO 转实体类、实体类转 VO
     *
     * @param source      原始对象
     * @param targetClass 目标类
     * @param <T>         目标类类型
     * @return 目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        T target = instantiateClass(targetClass);
        copyProperties(source, target);
        return target;
    }

    /**
     * 转换原始对象集合到目标类型的对象集合
     *
     * @param sources     原始对象集合
     * @param targetClass 目标类
     * @param <T>         目标类类型
     * @return 目标对象集合
     */
    public static <T> List<T> convert(Collection<?> sources, Class<T> targetClass) {
        return sources.stream().map(s -> convert(s, targetClass)).collect(Collectors.toList());
    }

    /**
     * 转换对象集合为 {@link Map}，指定一个属性的值作为键，对象本身作为值。
     * 如果键重复，后者的值覆盖前者。
     *
     * @param beans       对象集合
     * @param keyProperty 作为键的属性名
     * @param <K>         键类型
     * @param <T>         值类型
     * @return 转换成的 {@link Map} 对象
     */
    public static <K, T> Map<K, T> convert(Collection<T> beans, String keyProperty) {
        return innerConvert(beans, keyProperty, null);
    }

    /**
     * 转换对象集合为 {@link Map}，指定一个属性的值作为键，指定另一个属性的值作为 {@link Map} 值。
     *
     * @param beans         对象集合
     * @param keyProperty   作为键的属性名
     * @param valueProperty 作为值的属性名
     * @param <K>           键类型
     * @param <V>           值类型
     * @return 转换成的 {@link Map }对象
     */
    public static <K, V> Map<K, V> convert(Collection<?> beans, String keyProperty, String valueProperty) {
        return innerConvert(beans, keyProperty, valueProperty);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> innerConvert(Collection<?> beans, String keyProperty, @Nullable String valueProperty) {
        return beans.stream().collect(
                LinkedHashMap::new,
                (map, bean) -> map.put(
                        (K) getPropertyValue(bean, keyProperty),
                        (V) (valueProperty == null ? bean : getPropertyValue(bean, valueProperty))
                ),
                LinkedHashMap::putAll
        );
    }

    /**
     * 按指定属性分组对象集合
     *
     * @param beans       对象集合
     * @param keyProperty 作为键的属性名
     * @param <K>         键类型
     * @param <T>         值类型
     * @return 分组后的 {@link Map} 对象
     */
    @SuppressWarnings("unchecked")
    public static <K, T> Map<K, List<T>> groupingBy(Collection<T> beans, String keyProperty) {
        return beans.stream().collect(Collectors.groupingBy(
                bean -> (K) BeanUtils.getPropertyValue(bean, keyProperty),
                Collectors.toList()
        ));
    }

    /**
     * 按指定属性分组对象集合
     *
     * @param beans         对象集合
     * @param keyProperty   作为键的属性名
     * @param valueProperty 作为值的属性名
     * @param <K>           键的属性值类型
     * @param <T>           值的属性值类型
     * @return 分组后的 {@link Map} 对象
     */
    @SuppressWarnings("unchecked")
    public static <K, T> Map<K, List<T>> groupingBy(Collection<?> beans, String keyProperty, String valueProperty) {
        return beans.stream().collect(Collectors.groupingBy(
                bean -> (K) BeanUtils.getPropertyValue(bean, keyProperty),
                Collectors.mapping(bean -> (T) BeanUtils.getPropertyValue(bean, valueProperty), Collectors.toList())
        ));
    }

}
