package com.github.peacetrue.spring.beans;

import lombok.Data;
import lombok.ToString;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

/**
 * @author peace
 * @since 1.0
 **/
@Data
public class UserDTO {

    public static final List<String> PROPERTY_NAMES = Arrays.asList("id", "name", "password");
    public static final int PROPERTY_COUNT = PROPERTY_NAMES.size();

    @Order(0)
    private Long id;
    @Order(10)
    private String name;
    @Order(12)
    private String password;

}
