package com.github.peacetrue.spring.formatter.date;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * 自动的时间转换器。
 *
 * @author peace
 */
public class AutomaticTimeFormatter extends AbstractAutomaticDateFormatter<Time> {

    /** 使用默认的日期格式。 */
    public AutomaticTimeFormatter() {
        super("HH:mm:ss", "HH:mm");
    }

    /**
     * 使用自定义的日期格式。
     *
     * @param format  日期显示格式
     * @param parsers 日期解析格式集合
     */
    public AutomaticTimeFormatter(String format, String[] parsers) {
        super(format, parsers);
    }

    @Override
    protected Time parseLong(Long date, Locale locale) {
        return new Time(date);
    }

    @Override
    protected Time parseString(DateFormat parser, String date) throws ParseException {
        return new Time(parser.parse(date).getTime());
    }

}
