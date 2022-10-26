package com.github.peacetrue.spring.formatter.date;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.github.peacetrue.time.format.DateTimeFormatterUtils.FLEX_ISO_LOCAL_DATE_TIME;

/**
 * @author peace
 **/
@Slf4j
class AutomaticLocalDateTimeFormatterTest {

    @SneakyThrows
    @Test
    void parseTimestamp() {
        AutomaticLocalDateTimeFormatter formatter = new AutomaticLocalDateTimeFormatter(FLEX_ISO_LOCAL_DATE_TIME);
        LocalDateTime localDate = formatter.parse(String.valueOf(System.currentTimeMillis()), Locale.getDefault());
        log.info("parseTimestamp: {}", localDate);
    }

    @SneakyThrows
    @Test
    void parsePattern() {
        AutomaticLocalDateTimeFormatter formatter = new AutomaticLocalDateTimeFormatter(FLEX_ISO_LOCAL_DATE_TIME);
        LocalDateTime localDate = formatter.parse(LocalDateTime.now().toString(), Locale.getDefault());
        log.info("parsePattern: {}", localDate);
    }

    @SneakyThrows
    @Test
    void print() {
        AutomaticLocalDateTimeFormatter formatter = new AutomaticLocalDateTimeFormatter(
                FLEX_ISO_LOCAL_DATE_TIME
        );
        String localDate = formatter.print(LocalDateTime.now(), Locale.getDefault());
        log.info("print: {}", localDate);
    }
}
