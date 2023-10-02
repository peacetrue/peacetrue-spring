package com.github.peacetrue.spring.operator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 操作者配置属性。
 *
 * @author peace
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "peacetrue.operator")
public class OperatorProperties {

    /** 切面配置 */
    @NestedConfigurationProperty
    private Pointcut pointcut = new Pointcut();

    /** 切面属性 */
    @Getter
    @Setter
    public static class Pointcut {
        /** 启用切面 */
        private boolean enabled = true;
        /** 切面正则表达式，键为包名，值为方法名，通过一个特定的键 enabled 启用切面配置。 */
        private Map<String, String[]> patterns = new LinkedHashMap<>();
    }

    String[] resolvePointcutPatterns() {
        return pointcut.patterns.entrySet().stream()
                .flatMap(entry -> {
                    String className = Pattern.quote(entry.getKey()) + "(\\..+)?\\.";
                    return Stream.of(entry.getValue()).map(method -> className + method);
                })
                .toArray(String[]::new);
    }

}
