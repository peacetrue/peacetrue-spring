package com.github.peacetrue.spring.web.servlet;

import com.github.peacetrue.servlet.CachedBodyFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;

/**
 * 缓存请求体过滤器自动配置。
 *
 * @author peace
 **/
@Configuration
@ConditionalOnClass(CachedBodyFilter.class)
@ConditionalOnProperty("peacetrue.web.cached-body-path-patterns")
@EnableConfigurationProperties(CachedBodyFilterProperties.class)
public class CachedBodyFilterAutoConfiguration {

    public static final String CACHED_BODY_FILTER_NAME = "cachedBodyFilter";
    public static final int CACHED_BODY_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE;

    private final CachedBodyFilterProperties properties;

    public CachedBodyFilterAutoConfiguration(CachedBodyFilterProperties properties) {
        this.properties = properties;
    }

    @Bean(name = CACHED_BODY_FILTER_NAME)
    @ConditionalOnMissingBean(name = CACHED_BODY_FILTER_NAME)
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilter() {
        FilterRegistrationBean<CachedBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachedBodyFilter());
        registrationBean.addServletNames(CACHED_BODY_FILTER_NAME);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.addUrlPatterns(properties.getCachedBodyPathPatterns());
        registrationBean.setOrder(CACHED_BODY_FILTER_ORDER);
        return registrationBean;
    }

}
