package com.github.peacetrue.spring.beans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;


/**
 * @author peace
 * @since 1.0
 **/
public class TempTest {

    private static final Method GET_CLASS_METHOD =
            Objects.requireNonNull(ReflectionUtils.findMethod(Object.class, "getClass"));


    /**
     * 获取指定类的属性名
     *
     * @param clazz 指定类
     * @return 属性名数组
     */
    public static String[] getPropertyNames(Class<?> clazz) {
        return getPropertyNames(getPropertyDescriptors(clazz));
    }

    /**
     * 获取属性名数组
     *
     * @param properties 属性集合
     * @return 属性名数组
     */
    public static String[] getPropertyNames(PropertyDescriptor... properties) {
        return Arrays.stream(properties)
                .map(FeatureDescriptor::getName)
                .filter(name -> !"class".equals(name))
                .toArray(String[]::new);
    }

    @Test
    void name() {
        Assertions.assertNotEquals(
                BeanUtils.getPropertyDescriptor(UserDTO.class, "id"),
                BeanUtils.getPropertyDescriptor(User.class, "id")
        );
    }

    /**
     * 排除 {@code class} 属性，{@code class} 属性源至 {@link Object#getClass()} 方法
     *
     * @param properties 属性数组
     * @return 排除 {@code class} 属性后的数组
     */
    private static PropertyDescriptor[] excludeGetClass(PropertyDescriptor[] properties) {
        return Arrays.stream(properties)
                .filter(descriptor -> "class".equals(descriptor.getName()))
                .toArray(PropertyDescriptor[]::new);
    }

    /**
     * 获取指定类的属性数组，不包含 {@link Object#getClass()} 属性
     *
     * @param clazz 指定类
     * @return 属性数组
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        return excludeGetClass(org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz));
    }


    /**
     * 获取指定类的属性，不存在抛出异常
     *
     * @param clazz        指定类
     * @param propertyName 属性名
     * @return 属性
     */
    public static PropertyDescriptor getRequiredPropertyDescriptor(Class<?> clazz, String propertyName) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, propertyName);
        if (pd == null) throwPropertyNotFoundException(clazz, propertyName);
        return pd;
    }

    private static void throwPropertyNotFoundException(Class<?> beanClass, String propertyName) {
        throw new IllegalArgumentException("Can't found property['" + propertyName + "'] on class[" + beanClass.getName() + "]");
    }

}
