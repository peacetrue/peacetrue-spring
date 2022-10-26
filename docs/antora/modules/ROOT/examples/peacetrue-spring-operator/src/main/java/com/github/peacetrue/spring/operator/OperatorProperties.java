package com.github.peacetrue.spring.operator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
@ToString
@ConfigurationProperties(prefix = "peacetrue.operator")
public class OperatorProperties {

    /** 切面正则表达式，键为包名，值为方法名，通过一个特定的键 enabled 启用切面配置。 */
    private Map<String, String[]> pointcutPatterns = new LinkedHashMap<>();

    public String[] resolvePointcutPatterns() {
        return pointcutPatterns.entrySet().stream()
                .filter(entry -> !"enabled".equals(entry.getKey()))
                .flatMap(entry -> {
                    String className = Pattern.quote(entry.getKey()) + "(\\..+)?\\.";
                    return Stream.of(entry.getValue()).map(method -> className + method);
                })
                .toArray(String[]::new);
    }

}
