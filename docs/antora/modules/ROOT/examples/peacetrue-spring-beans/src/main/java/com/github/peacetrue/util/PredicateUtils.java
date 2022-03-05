package com.github.peacetrue.util;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * 断言工具类
 *
 * @author peace
 * @since 1.0
 **/
public abstract class PredicateUtils {

    /** 抽象工具类，无需实例化 */
    protected PredicateUtils() {
    }

    /**
     * 判断取反
     *
     * @param predicate 判断
     * @param <T>       判断的参数类型
     * @return 反向的判断
     */
    public static <T> Predicate<T> negate(Predicate<T> predicate) {
        return predicate.negate();
    }


    /**
     * 一个参数始终为 {@code true} 的断言函数
     *
     * @param first 第一个参数
     * @param <T>   断言函数第一个参数的类型
     * @return {@code true}
     */
    public static <T> boolean alwaysTrue(T first) {
        return true;
    }

    /**
     * 二个参数始终为 {@code true} 的断言函数
     *
     * @param first  第一个参数
     * @param second 第二个参数
     * @param <T>    断言函数第一个参数的类型
     * @param <U>    断言函数第二个参数的类型
     * @return {@code true}
     */
    public static <T, U> boolean alwaysTrue(T first, U second) {
        return true;
    }

    /**
     * 将一个参数的断言扩展为二个参数的断言，实际使用第一个参数
     *
     * @param predicate 断言函数
     * @param <T>       断言函数第一个参数的类型
     * @param <U>       断言函数第二个参数的类型
     * @return 二个参数的断言
     */
    public static <T, U> BiPredicate<T, U> extendWithFirst(Predicate<T> predicate) {
        return (first, second) -> predicate.test(first);
    }

    /**
     * 将一个参数的断言扩展为二个参数的断言，实际使用第二个参数
     *
     * @param predicate 断言函数
     * @param <T>       断言函数第一个参数的类型
     * @param <U>       断言函数第二个参数的类型
     * @return 二个参数的断言
     */
    public static <T, U> BiPredicate<T, U> extendWithSecond(Predicate<U> predicate) {
        return (left, right) -> predicate.test(right);
    }

}
