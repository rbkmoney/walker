package com.rbkmoney.walker.utils;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * @since 27.03.17
 **/
public class TimeUtils {
    private static final DateTimeFormatter FORMATTER = ISO_INSTANT;

    public static String timestampToString(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ISO_INSTANT
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(timestamp.getTime()));
    }

    public static TemporalAccessor stringToTemporal(String dateTimeStr) throws IllegalArgumentException {
        try {
            return FORMATTER.parse(dateTimeStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse: " + dateTimeStr, e);
        }
    }

    public static Timestamp toTimestamp(String time) {
        return Timestamp.from(Instant.from(stringToTemporal(time)));
    }


}
