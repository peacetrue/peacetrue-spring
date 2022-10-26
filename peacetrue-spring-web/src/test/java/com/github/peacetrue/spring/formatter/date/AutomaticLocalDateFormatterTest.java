package com.github.peacetrue.spring.formatter.date;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static com.github.peacetrue.time.format.DateTimeFormatterUtils.FLEX_ISO_LOCAL_DATE_TIME;

/**
 * @author peace
 **/
@Slf4j
class AutomaticLocalDateFormatterTest {

    @SneakyThrows
    @Test
    void parseTimestamp() {
        AutomaticLocalDateFormatter formatter = new AutomaticLocalDateFormatter(FLEX_ISO_LOCAL_DATE_TIME);
        LocalDate localDate = formatter.parse(String.valueOf(System.currentTimeMillis()), Locale.getDefault());
        log.info("localDate: {}", localDate);
    }

    @SneakyThrows
    @Test
    void parsePattern() {
        AutomaticLocalDateFormatter formatter = new AutomaticLocalDateFormatter(FLEX_ISO_LOCAL_DATE_TIME);
        LocalDate localDate = formatter.parse(LocalDate.now().toString(), Locale.getDefault());
        log.info("localDate: {}", localDate);
    }

    @SneakyThrows
    @Test
    void print() {
        AutomaticLocalDateFormatter formatter = new AutomaticLocalDateFormatter(FLEX_ISO_LOCAL_DATE_TIME);
        String localDate = formatter.print(LocalDate.now(), Locale.getDefault());
        log.info("localDate: {}", localDate);
    }
}
