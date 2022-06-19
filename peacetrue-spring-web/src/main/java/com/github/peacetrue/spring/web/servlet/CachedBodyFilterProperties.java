package com.github.peacetrue.spring.web.servlet;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author peace
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "peacetrue.web")
public class CachedBodyFilterProperties {
    private String[] cachedBodyPathPatterns;
}
