package com.github.peacetrue.spring.operator;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;

import java.util.Objects;

/**
 * 操作者织入后置处理器。
 *
 * @author peace
 **/
public class OperatorAdvisingPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

    /**
     * 初始设置通知者
     *
     * @param advisor 通知者
     */
    public OperatorAdvisingPostProcessor(Advisor advisor) {
        this.advisor = Objects.requireNonNull(advisor);
    }

}
