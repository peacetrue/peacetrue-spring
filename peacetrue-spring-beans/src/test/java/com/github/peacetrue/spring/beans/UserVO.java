package com.github.peacetrue.spring.beans;

import lombok.Data;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author peace
 * @since 1.0
 **/
@Data
public class UserVO {

    public static final List<String> PROPERTY_NAMES =
            Arrays.asList("id", "name", "password", "role", "roles", "roleNames", "creatorId", "createdTime");
    public static final int PROPERTY_COUNT = PROPERTY_NAMES.size();

    @Order(0)
    private Long id;
    @Order(10)
    private String name;
    @Order(20)
    private String password;
    @Order(25)
    private Role role;
    @Order(25)
    private List<Role> roles;
    @Order(26)
    private List<String> roleNames;
    @Order(30)
    private Long creatorId;
    @Order(40)
    private LocalDateTime createdTime;

}
