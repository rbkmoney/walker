package com.rbkmoney.walker.utils;

import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @since 27.03.17
 **/
public class TimeUtils {

    public static Timestamp toTimestamp(String time) {
        return Timestamp.from(Instant.from(TemporalConverter.stringToTemporal(time)));
    }


    public static String timestampToString(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ISO_INSTANT
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(timestamp.getTime()));
    }

}
