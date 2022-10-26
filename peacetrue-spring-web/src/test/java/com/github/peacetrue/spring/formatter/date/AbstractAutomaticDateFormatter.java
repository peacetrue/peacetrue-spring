package com.github.peacetrue.spring.formatter.date;

import org.springframework.format.Formatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自动的日期转换器。
 * <ul>
 * <li>如果输入为数值，根据毫秒数转换</li>
 * <li>如果输入不为数值，根据输入值的长度查找对应的日期规则，进行转换</li>
 * </ul>
 *
 * @param <T> 日期类型
 * @author peace
 */
public abstract class AbstractAutomaticDateFormatter<T extends Date> implements Formatter<T> {

    /** 日期解析格式集合。 */
    protected final Map<Integer, String> parsers;
    /** 日期显示格式。 */
    protected final String format;

    /**
     * 日期显示精确到秒。
     *
     * @param parsers 日期解析格式集合
     * @see DateTimeFormatter#ISO_LOCAL_DATE_TIME
     */
    public AbstractAutomaticDateFormatter(String... parsers) {
        this("yyyy-MM-dd'T'HH:mm:ss", parsers);
    }

    /**
     * 自定义日期显示格式和日期解析格式。
     *
     * @param format  日期显示格式
     * @param parsers 日期解析格式集合
     */
    protected AbstractAutomaticDateFormatter(String format, String[] parsers) {
        this.format = Objects.requireNonNull(format);
        this.parsers = Stream.of(parsers).collect(Collectors.toMap(String::length, Function.identity()));
    }

    @Override
    public T parse(String text, Locale locale) throws ParseException {
        text = text.trim();
        if (text.chars().allMatch(Character::isDigit)) {
            return parseLong(Long.parseLong(text), locale);
        }

        String pattern = parsers.get(text.length());
        if (pattern == null) {
            throw new IllegalArgumentException("Can't found format pattern which is matched '" + text + "'");
        }

        return parseString(new SimpleDateFormat(pattern), text);
    }

    /**
     * 解析时间戳。
     *
     * @param date   时间戳
     * @param locale 方言
     * @return 日期
     */
    protected abstract T parseLong(Long date, Locale locale);

    /**
     * 解析字符串日期。
     *
     * @param parser 解析器
     * @param date   字符串日期
     * @return 日期
     * @throws ParseException 字符串日期格式有误导致无法解析
     */
    protected abstract T parseString(DateFormat parser, String date) throws ParseException;

    @Override
    public String print(T object, Locale locale) {
        return new SimpleDateFormat(format).format(object);
    }
}
