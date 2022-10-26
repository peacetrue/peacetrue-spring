package com.github.peacetrue.spring.operator;

import com.github.peacetrue.operator.OperatorSupplier;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

/**
 * 操作者自动配置。
 *
 * @author peace
 **/
@Configuration
public class OperatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OperatorSupplier.class)
    public OperatorSupplier operatorSupplier() {
        String security = "org.springframework.security.core.context.SecurityContextHolder";
        boolean securityPresent = ClassUtils.isPresent(security, this.getClass().getClassLoader());
        return getOperatorSupplier(securityPresent);
    }

    static OperatorSupplier getOperatorSupplier(boolean securityPresent) {
        return securityPresent ? new SpringSecurityOperatorSupplier(OperatorSupplier.SYSTEM) : OperatorSupplier.SYSTEM_SUPPLIER;
    }

    /**
     * 操作者切面配置。
     */
    @Configuration
    @ConditionalOnProperty(name = "peacetrue.operator.pointcut.enabled", havingValue = "true")
    @EnableConfigurationProperties(OperatorProperties.class)
    public static class OperatorPointcutConfiguration {

        private final OperatorProperties properties;

        public OperatorPointcutConfiguration(OperatorProperties properties) {
            this.properties = properties;
        }

        @Bean
        public MethodInterceptor operatorMethodInterceptor() {
            return new OperatorMethodInterceptor();
        }

        @Bean
        public Pointcut operatorPointcut() {
            JdkRegexpMethodPointcut methodMatcher = new JdkRegexpMethodPointcut();
            methodMatcher.setPatterns(properties.resolvePointcutPatterns());
            return new ComposablePointcut(ClassFilter.TRUE, methodMatcher);
        }

        @Bean
        @ConditionalOnBean(name = "operatorPointcut")
        public OperatorAdvisingPostProcessor operatorPostProcessor(Pointcut operatorPointcut,
                                                                   MethodInterceptor operatorMethodInterceptor) {
            return new OperatorAdvisingPostProcessor(
                    new DefaultPointcutAdvisor(operatorPointcut, operatorMethodInterceptor)
            );
        }
    }

}
