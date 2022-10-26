package com.github.peacetrue.spring.formatter.date;

import org.springframework.format.Formatter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Objects;

/**
 * 自动的日期转换器。
 * <ul>
 * <li>如果输入为数值，根据毫秒数转换</li>
 * <li>如果输入不为数值，根据输入值的长度查找对应的日期规则，进行转换</li>
 * </ul>
 *
 * @author peace
 */
public abstract class AbstractAutomaticLocalDateFormatter<T extends TemporalAccessor> implements Formatter<T> {

    /** 解析器 */
    protected final DateTimeFormatter parser;
    /** 格式器 */
    protected final DateTimeFormatter formatter;

    /**
     * 使用相同的解析器和格式器。
     *
     * @param parserFormatter 解析器或格式器
     */
    protected AbstractAutomaticLocalDateFormatter(DateTimeFormatter parserFormatter) {
        this(parserFormatter, parserFormatter);
    }

    /**
     * 使用不同的解析器和格式器。
     *
     * @param parser    解析器
     * @param formatter 格式化器
     */
    protected AbstractAutomaticLocalDateFormatter(DateTimeFormatter parser, DateTimeFormatter formatter) {
        this.parser = Objects.requireNonNull(parser);
        this.formatter = Objects.requireNonNull(formatter);
    }

    @Override
    public T parse(String text, Locale locale) {
        text = text.trim();
        if (text.chars().allMatch(Character::isDigit)) {
            return parseTimestamp(Long.parseLong(text), locale);
        }
        return parsePattern(text, locale);
    }

    /**
     * 解析时间戳。
     *
     * @param milli  毫秒时间戳
     * @param locale 方言
     * @return 日期时间
     */
    protected abstract T parseTimestamp(Long milli, Locale locale);

    /**
     * 解析字符串日期。
     *
     * @param time   字符串日期
     * @param locale 方言
     * @return 日期时间
     */
    protected abstract T parsePattern(String time, Locale locale);

    @Override
    public String print(T object, Locale locale) {
        return formatter.format(object);
    }
}
