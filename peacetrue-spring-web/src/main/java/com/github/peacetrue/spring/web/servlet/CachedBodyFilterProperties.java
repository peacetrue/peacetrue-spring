package com.github.peacetrue.spring.web.servlet;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 缓存请求体过滤器配置属性。
 *
 * @author peace
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "peacetrue.web")
public class CachedBodyFilterProperties {
    /** 缓存请求体的请求路径规则，Filter 风格，/* 表示所有 */
    private String[] cachedBodyPathPatterns;
}
