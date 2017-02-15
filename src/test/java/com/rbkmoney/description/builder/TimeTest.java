package com.rbkmoney.description.builder;

import com.rbkmoney.walker.service.DescriptionBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 14.02.17
 **/
public class TimeTest {

    @Test
    public void timeTest() {
        String s = DescriptionBuilder.toPrettyDate("2017-02-13T18:24:02.346830Z");
        assertEquals("13.02.2017", s);
    }
}
