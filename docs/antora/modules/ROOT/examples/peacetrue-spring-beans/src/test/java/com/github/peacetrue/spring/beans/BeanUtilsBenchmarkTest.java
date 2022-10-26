package com.github.peacetrue.spring.beans;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author peace
 **/
public class BeanUtilsBenchmarkTest {

    @Data
    @ToString
    public static class TestBean {
        private Byte bytes;
        private Short shorts;
        private Integer ints;
        private Long longs;
        private Boolean booleans;
        private Float floats;
        private Double doubles;
        private Character character;
        private String string;
        private Date date;
        private LocalDateTime localDateTime;
    }

}
