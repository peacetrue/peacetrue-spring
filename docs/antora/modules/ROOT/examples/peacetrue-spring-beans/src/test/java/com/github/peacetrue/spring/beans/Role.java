package com.github.peacetrue.spring.beans;

import lombok.Data;
import lombok.ToString;
import org.springframework.core.annotation.Order;

/**
 * @author peace
 * @since 1.0
 **/
@Data
@ToString
public class Role {

    @Order(0)
    private Long id;
    @Order(1)
    private String name;
    @Order(2)
    private Long userId;

}
