package com.github.peacetrue.spring.formatter.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * 自动的日期转换器。
 *
 * @author peace
 */
public class AutomaticDateFormatter extends AbstractAutomaticDateFormatter<Date> {

    /** 使用默认的日期格式。 */
    public AutomaticDateFormatter() {
        super("yyyy-MM", "yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 使用自定义的日期格式。
     *
     * @param parsers 日期格式集合
     */
    public AutomaticDateFormatter(String... parsers) {
        super(parsers);
    }

    /**
     * 使用自定义的日期格式。
     *
     * @param parsers 日期格式集合
     * @param format   日期格式化器
     */
    public AutomaticDateFormatter(String[] parsers, String format) {
        super(format, parsers);
    }

    @Override
    protected Date parseLong(Long date, Locale locale) {
        return new Date(date);
    }

    @Override
    protected Date parseString(DateFormat parser, String date) throws ParseException {
        return parser.parse(date);
    }

}
