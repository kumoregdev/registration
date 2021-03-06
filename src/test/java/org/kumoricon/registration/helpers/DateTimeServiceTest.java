package org.kumoricon.registration.helpers;


import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class DateTimeServiceTest {
    private final DateTimeService dateTimeService = new DateTimeService();

    @Test
    public void format() {
        assertEquals("12/31/1969 04:00:00 PM PST", dateTimeService.format(Instant.ofEpochMilli(0L)));
    }

    @Test
    public void formatOffsetDateTime() {
        assertEquals("02/29/2020 04:13:14 AM PST",
                dateTimeService.format(OffsetDateTime.of(2020, 2, 29, 12, 13, 14, 0, ZoneOffset.UTC)));
    }

    @Test
    public void formatLocalDate() {
        assertEquals("02/29/2020",
                dateTimeService.format(LocalDate.of(2020, 2, 29)));
    }

    @Test
    public void epochToDateString() {
        assertEquals("12/31/1969 04:00:00 PM PST", dateTimeService.epochToDateString(0L));
    }

    @Test
    public void epochToDurationForward() {
        assertEquals("1 day 1:00:00 ago",
                dateTimeService.epochToDuration(0L, 90000000L));
        assertEquals("20 days 20:02:30 ago", dateTimeService.epochToDuration(0L, 1800150500L));
        assertEquals("8 minutes ago", dateTimeService.epochToDuration(0L, 500000L));
        assertEquals("1 minute ago", dateTimeService.epochToDuration(0L, 65000L));
        assertEquals("30 seconds ago", dateTimeService.epochToDuration(0L, 30000L));
        assertEquals("1 second ago", dateTimeService.epochToDuration(0L, 1000L));
    }

    @Test
    public void epochToDurationBackwards() {
        assertThrows(AssertionError.class, () -> dateTimeService.epochToDuration(10L, 0L));
    }

    @Test
    public void epochToDurationNull() {
        assertThrows(AssertionError.class, () -> dateTimeService.epochToDuration(null));
    }

    @Test
    public void epochToDurationSame() {
        assertEquals("Now", dateTimeService.epochToDuration(50L, 50L));
    }
}