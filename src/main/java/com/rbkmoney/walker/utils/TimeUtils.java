package com.rbkmoney.walker.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String toIsoInstantString(LocalDateTime localDateTime) {
        return ZonedDateTime.ofLocal(localDateTime, ZoneId.of("Z"), null).format(DateTimeFormatter.ISO_INSTANT);
    }

}
