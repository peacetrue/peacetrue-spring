package com.github.peacetrue.spring.core.io.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;

/**
 * @author peace
 **/
class YamlPropertySourceFactoryTest {

    @Test
    void createPropertySource() {
        YamlPropertySourceFactory factory = new YamlPropertySourceFactory();
        EncodedResource encodedResource = new EncodedResource(new ClassPathResource("YamlPropertySourceFactory.yml"));
        PropertySource<?> propertySource = factory.createPropertySource("test", encodedResource);
        Assertions.assertEquals("name", propertySource.getProperty("name"));
    }
}
