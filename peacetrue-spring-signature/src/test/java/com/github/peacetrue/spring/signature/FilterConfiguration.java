package com.github.peacetrue.spring.signature;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;

import static com.github.peacetrue.spring.web.servlet.CachedBodyFilterAutoConfiguration.CACHED_BODY_FILTER_NAME;

@Configuration
@ConditionalOnMissingBean(name = CACHED_BODY_FILTER_NAME)
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<SignatureServerFilter> signatureServerFilter(
            SignatureServerService signatureVerifyService, SignatureProperties properties
    ) {
        FilterRegistrationBean<SignatureServerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SignatureServerFilter(signatureVerifyService));
        registrationBean.addServletNames("SignatureServerFilter");
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.addUrlPatterns(properties.getVerifyPathPatterns());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return registrationBean;
    }
}
