package com.github.peacetrue.spring.beans;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ToString
public class TestBean {
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
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private BigDecimal bigDecimal;
}
