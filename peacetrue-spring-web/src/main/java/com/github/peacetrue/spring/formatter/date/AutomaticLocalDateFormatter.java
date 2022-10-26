package com.github.peacetrue.spring.formatter.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 自动的日期转换器。
 *
 * @author peace
 */
public class AutomaticLocalDateFormatter extends AbstractAutomaticLocalDateFormatter<LocalDate> {

    public AutomaticLocalDateFormatter(DateTimeFormatter parserFormatter) {
        super(parserFormatter);
    }

    public AutomaticLocalDateFormatter(DateTimeFormatter parser, DateTimeFormatter formatter) {
        super(parser, formatter);
    }

    @Override
    protected LocalDate parseTimestamp(Long milli, Locale locale) {
        return Instant.ofEpochMilli(milli).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    protected LocalDate parsePattern(String time, Locale locale) {
        return LocalDate.parse(time, parser);
    }

}
