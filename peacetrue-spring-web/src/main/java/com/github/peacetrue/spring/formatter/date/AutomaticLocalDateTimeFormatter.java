package com.github.peacetrue.spring.formatter.date;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 自动的日期时间转换器。
 *
 * @author peace
 */
public class AutomaticLocalDateTimeFormatter extends AbstractAutomaticLocalDateFormatter<LocalDateTime> {

    public AutomaticLocalDateTimeFormatter(DateTimeFormatter parserFormatter) {
        super(parserFormatter);
    }

    public AutomaticLocalDateTimeFormatter(DateTimeFormatter parser, DateTimeFormatter formatter) {
        super(parser, formatter);
    }

    @Override
    protected LocalDateTime parseTimestamp(Long milli, Locale locale) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault());
    }

    @Override
    protected LocalDateTime parsePattern(String time, Locale locale) {
        return LocalDateTime.parse(time, parser);
    }

}
