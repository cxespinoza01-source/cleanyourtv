package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.Instant;

import org.junit.Test;

public class DateTimesTest {

    @Test
    public void parsesStrictGregorianCalendarDates() {
        assertEquals(20261021, DateTimes.parseDateKey("2026-10-21"));
        try {
            DateTimes.parseDateKey("2026-2-1");
            fail("non-ISO date should be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            DateTimes.parseDateKey("2026-02-30");
            fail("impossible date should be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void parsesUtcAndOffsetIsoTimestamps() {
        assertEquals(Instant.parse("2026-10-23T17:30:00Z").toEpochMilli(),
                DateTimes.parseIsoMillis("2026-10-23T17:30:00Z"));
        assertEquals(Instant.parse("2026-10-23T17:30:00.123Z").toEpochMilli(),
                DateTimes.parseIsoMillis("2026-10-23T17:30:00.123Z"));
        assertEquals(Instant.parse("2026-10-23T17:30:00Z").toEpochMilli(),
                DateTimes.parseIsoMillis("2026-10-23T19:30:00+02:00"));
    }

    @Test
    public void rejectsUnparseableTimestamps() {
        try {
            DateTimes.parseIsoMillis("not-a-date");
            fail("invalid timestamp should be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}
