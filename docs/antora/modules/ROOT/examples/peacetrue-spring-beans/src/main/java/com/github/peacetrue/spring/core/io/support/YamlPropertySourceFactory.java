package com.github.peacetrue.spring.core.io.support;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * 用于加载 Yaml 属性文件。
 *
 * <pre>
 * &nbsp;@PropertySource(value = "classpath:/application-template-service.yml", factory = YamlPropertySourceFactory.class)
 * </pre>
 *
 * @author peace
 **/
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());
        return new PropertiesPropertySource(
                Objects.requireNonNull(encodedResource.getResource().getFilename()),
                Objects.requireNonNull(factory.getObject())
        );
    }
}
