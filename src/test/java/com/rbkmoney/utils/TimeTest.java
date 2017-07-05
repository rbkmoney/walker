package com.rbkmoney.utils;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.junit.Assert.assertEquals;

/**
 * @since 14.02.17
 **/
public class TimeTest {

    public static String toPrettyDate(String time) {
        try {
            Instant instant = Instant.from(ISO_INSTANT.parse(time));
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return formatter.format(localDateTime);
        } catch (DateTimeParseException | NullPointerException e) {
            return time;
        }
    }

    @Test
    public void timeTest() {
        String s = toPrettyDate("2017-02-13T18:24:02.346830Z");
        assertEquals("13.02.2017", s);
    }

    @Test
    public void timeWrongTest() {
        String s = toPrettyDate("2017-02-13T18:24:02.346830ZBITCH");
        assertEquals("2017-02-13T18:24:02.346830ZBITCH", s);
    }

    @Test
    public void timeNullTest() {
        String s = toPrettyDate(null);
        assertEquals(null, s);
    }
}
