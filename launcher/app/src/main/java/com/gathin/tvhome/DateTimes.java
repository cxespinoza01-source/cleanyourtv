package com.gathin.tvhome;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Small java.util date helpers so the launcher needs no java.time backport on Android 7. */
final class DateTimes {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private DateTimes() {}

    /** A calendar date encoded as yyyyMMdd, independent of the machine's locale. */
    static int dateKey(int year, int month, int day) {
        return year * 10000 + month * 100 + day;
    }

    static int todayKey() {
        Calendar c = Calendar.getInstance();
        return dateKey(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    static int parseDateKey(String value) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        f.setLenient(false);
        f.setTimeZone(UTC);
        ParsePosition p = new ParsePosition(0);
        Date parsed = f.parse(value, p);
        if (parsed == null || p.getIndex() != value.length()) throw new IllegalArgumentException("Invalid date");
        Calendar c = Calendar.getInstance(UTC, Locale.ROOT);
        c.setTime(parsed);
        return dateKey(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    static int dayOfYear(int dateKey) {
        Calendar c = Calendar.getInstance(UTC, Locale.ROOT);
        c.clear();
        c.set(dateKey / 10000, (dateKey / 100) % 100 - 1, dateKey % 100, 12, 0, 0);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    static int offsetMinutesAtNoon(int dateKey, TimeZone zone) {
        Calendar c = Calendar.getInstance(zone, Locale.ROOT);
        c.clear();
        c.set(dateKey / 10000, (dateKey / 100) % 100 - 1, dateKey % 100, 12, 0, 0);
        return zone.getOffset(c.getTimeInMillis()) / 60000;
    }

    /** Parses ISO-8601 UTC/offset timestamps used by the optional race calendar. */
    static long parseIsoMillis(String value) {
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
        };
        for (String pattern : patterns) {
            SimpleDateFormat f = new SimpleDateFormat(pattern, Locale.ROOT);
            f.setLenient(false);
            f.setTimeZone(UTC);
            ParsePosition p = new ParsePosition(0);
            Date d = f.parse(value, p);
            if (d != null && p.getIndex() == value.length()) return d.getTime();
        }
        throw new IllegalArgumentException("Invalid ISO-8601 timestamp");
    }

    static String formatRaceTime(long millis, boolean twentyFourHour, Locale locale, TimeZone zone) {
        SimpleDateFormat f = new SimpleDateFormat(twentyFourHour ? "EEE HH:mm" : "EEE h:mm a", locale);
        f.setTimeZone(zone);
        return f.format(new Date(millis));
    }
}
