/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.extra.chrono

import org.testng.*;
import org.testng.annotations.*;
import org.threeten.bp.*;
import org.threeten.bp.chrono.*;
import org.threeten.bp.temporal.*;
import spock.lang.Specification;
import static org.testng.Assert.*;
import static org.threeten.extra.chrono.DiscordianChronology.*;
import static org.threeten.extra.chrono.DiscordianDate.*;

class DiscordianChronologySpec extends Specification {

    //-----------------------------------------------------------------------
    // Chrono.ofName("Discordian")  Lookup by name
    //-----------------------------------------------------------------------
    public void test_chrono_byName() {
        Chronology c = DiscordianChronology.INSTANCE;
        Chronology test = Chronology.of("Discordian");
        Assert.assertNotNull(test, "The Discordian calendar could not be found byName");
        Assert.assertEquals(test.getId(), "Discordian", "ID mismatch");
        Assert.assertEquals(test.getCalendarType(), null, "Type mismatch");
        Assert.assertEquals(test, c);
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
                // YOLD era starts in 1166 BC
                {DiscordianChronology.INSTANCE.date(0, 1, 1), LocalDate.of(-1166, 1, 1)},
                // sanity check current date
                {DiscordianChronology.INSTANCE.date(3179, 2, 19), LocalDate.of(2013, 4, 2)},
                // leap day occurs on day 60 of the year but is *not* considered part of the 1st season
                {DiscordianChronology.INSTANCE.date(3179, 1, 59), LocalDate.of(2013, 2, 28)},
                {DiscordianChronology.INSTANCE.date(3179, 1, 60), LocalDate.of(2013, 3, 1)}, // non leap year
                {DiscordianChronology.INSTANCE.date(3178, 1, 60), LocalDate.of(2012, 3, 1)}, // leap year
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), LocalDate.of(2012, 2, 29)},
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY + 1), LocalDate.of(2012, 3, 1)},
                // the season boundaries occur on the same Gregorian day every year...
                {DiscordianChronology.INSTANCE.date(3179, 1, 1), LocalDate.of(2013, 1, 1)},
                {DiscordianChronology.INSTANCE.date(3179, 1, 73), LocalDate.of(2013, 3, 14)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 1), LocalDate.of(2013, 3, 15)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 73), LocalDate.of(2013, 5, 26)},
                {DiscordianChronology.INSTANCE.date(3179, 3, 1), LocalDate.of(2013, 5, 27)},
                {DiscordianChronology.INSTANCE.date(3179, 3, 73), LocalDate.of(2013, 8, 7)},
                {DiscordianChronology.INSTANCE.date(3179, 4, 1), LocalDate.of(2013, 8, 8)},
                {DiscordianChronology.INSTANCE.date(3179, 4, 73), LocalDate.of(2013, 10, 19)},
                {DiscordianChronology.INSTANCE.date(3179, 5, 1), LocalDate.of(2013, 10, 20)},
                {DiscordianChronology.INSTANCE.date(3179, 5, 73), LocalDate.of(2013, 12, 31)},
                // ... even if it is a leap year
                {DiscordianChronology.INSTANCE.date(3178, 1, 1), LocalDate.of(2012, 1, 1)},
                {DiscordianChronology.INSTANCE.date(3178, 1, 73), LocalDate.of(2012, 3, 14)},
                {DiscordianChronology.INSTANCE.date(3178, 2, 1), LocalDate.of(2012, 3, 15)},
                {DiscordianChronology.INSTANCE.date(3178, 2, 73), LocalDate.of(2012, 5, 26)},
                {DiscordianChronology.INSTANCE.date(3178, 3, 1), LocalDate.of(2012, 5, 27)},
                {DiscordianChronology.INSTANCE.date(3178, 3, 73), LocalDate.of(2012, 8, 7)},
                {DiscordianChronology.INSTANCE.date(3178, 4, 1), LocalDate.of(2012, 8, 8)},
                {DiscordianChronology.INSTANCE.date(3178, 4, 73), LocalDate.of(2012, 10, 19)},
                {DiscordianChronology.INSTANCE.date(3178, 5, 1), LocalDate.of(2012, 10, 20)},
                {DiscordianChronology.INSTANCE.date(3178, 5, 73), LocalDate.of(2012, 12, 31)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_toLocalDate(ChronoLocalDate<?> ddate, LocalDate iso) {
        assertEquals(LocalDate.from(ddate), iso);
    }

    @Test(dataProvider = "samples")
    public void test_fromCalendrical(ChronoLocalDate<?> ddate, LocalDate iso) {
        assertEquals(DiscordianChronology.INSTANCE.date(iso), ddate);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
                // out of range season
                {3179, 0, 0},
                {3179, -1, 1},
                {3179, 6, 1},

                // out of range day
                {3179, 1, 0},
                {3179, 1, 74},
        };
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(int year, int season, int dayOfSeason) {
        DiscordianChronology.INSTANCE.date(year, season, dayOfSeason);
    }

    //-----------------------------------------------------------------------
    // with(WithAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_withLastDayOfMonth() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3179, 1, 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianChronology.INSTANCE.date(3179, 1, 73));
    }

    @Test
    public void test_withLastDayOfMonthInLeapYear() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3178, 1, ST_TIBS_DAY - 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianChronology.INSTANCE.date(3178, 1, 73));
    }

    @Test
    public void test_withLastDayOfMonthFromLeapDay() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianChronology.INSTANCE.date(3178, 1, 73));
    }

    @Test
    void test_withDayOfWeek() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3179, 1, 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastInMonth(DayOfWeek.of(1)));
        assertEquals(test, DiscordianChronology.INSTANCE.date(3179, 1, 71));
    }

    @Test
    void test_withDayOfWeekInLeapYear() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3178, 1, 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastInMonth(DayOfWeek.of(1)));
        assertEquals(test, DiscordianChronology.INSTANCE.date(3178, 1, 71));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name = "toString")
    Object[][] data_toString() {
        return new Object[][] {
                {DiscordianChronology.INSTANCE.date(0, 1, 1), "Sweetmorn, Chaos 1, 0 YOLD"},
                {DiscordianChronology.INSTANCE.date(3179, 2, 19), "Boomtime, Discord 19, 3179 YOLD"},
                // 60th day of the year is St. Tib's Day if it is a leap year
                {DiscordianChronology.INSTANCE.dateYearDay(3178, 60), "St. Tib's Day! 3178 YOLD"},
                {DiscordianChronology.INSTANCE.dateYearDay(3179, 60), "Setting Orange, Chaos 60, 3179 YOLD"},
                // St. Tib's Day is not part of the week, so day names after it should be the same in a leap year
                {DiscordianChronology.INSTANCE.date(3179, 1, 60), "Setting Orange, Chaos 60, 3179 YOLD"},
                {DiscordianChronology.INSTANCE.date(3178, 1, 60), "Setting Orange, Chaos 60, 3178 YOLD"},
        };
    }

    @Test(dataProvider = "toString")
    public void test_toString(ChronoLocalDate<?> jdate, String expected) {
        assertEquals(jdate.toString(), expected);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_true() {
        assertTrue(DiscordianChronology.INSTANCE.equals(DiscordianChronology.INSTANCE));
    }

    @Test
    public void test_equals_false() {
        assertFalse(DiscordianChronology.INSTANCE.equals(IsoChronology.INSTANCE));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------

    @Test
    public void test_compareTo() {
        final ChronoLocalDate<?> ddate1 = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY);
        final ChronoLocalDate<?> ddate2 = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY + 1);
        assertTrue(ddate1.compareTo(ddate2) < 0);
        assertTrue(ddate2.compareTo(ddate1) > 0);
    }

    //-----------------------------------------------------------------------
    // isLeapYear(), field ranges in leap and non-leap years
    //-----------------------------------------------------------------------

    @DataProvider(name = "leapYears")
    Object[][] data_leapYears() {
        return new Object[][] {
                {DiscordianChronology.INSTANCE.date(3179, 1, 1), false},
                {DiscordianChronology.INSTANCE.date(3178, 1, 1), true},
                {DiscordianChronology.INSTANCE.date(3066, 1, 1), false},
                {DiscordianChronology.INSTANCE.date(3166, 1, 1), true},
        };
    }

    @Test(dataProvider = "leapYears")
    public void test_isLeapYear(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.isLeapYear(), isLeapYear);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(ddate.getYear()), isLeapYear);
    }

    @Test(dataProvider = "leapYears")
    public void test_dayOfWeekRange(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.range(ChronoField.DAY_OF_WEEK).getMinimum(), isLeapYear ? 0 : 1);
        assertEquals(ddate.range(ChronoField.DAY_OF_WEEK).getMaximum(), DAYS_PER_WEEK);
    }

    @Test(dataProvider = "leapYears")
    public void test_dayOfMonthRange(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.range(ChronoField.DAY_OF_MONTH).getMinimum(), isLeapYear ? 0 : 1);
        assertEquals(ddate.range(ChronoField.DAY_OF_MONTH).getMaximum(), DAYS_PER_SEASON);
    }

    @Test(dataProvider = "leapYears")
    public void test_monthOfYearRange(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.range(ChronoField.MONTH_OF_YEAR).getMinimum(), isLeapYear ? 0 : 1);
        assertEquals(ddate.range(ChronoField.MONTH_OF_YEAR).getMaximum(), SEASONS_PER_YEAR);
    }

    @Test
    public void test_dayOfWeekRangeOnChronology() {
        ValueRange range = DiscordianChronology.INSTANCE.range(ChronoField.DAY_OF_WEEK);
        assertEquals(range.getMinimum(), 0);
        assertEquals(range.getLargestMinimum(), 1);
        assertEquals(range.getSmallestMaximum(), DAYS_PER_WEEK);
        assertEquals(range.getMaximum(), DAYS_PER_WEEK);
    }

    @Test
    public void test_dayOfMonthRangeOnChronology() {
        ValueRange range = DiscordianChronology.INSTANCE.range(ChronoField.DAY_OF_MONTH);
        assertEquals(range.getMinimum(), 0);
        assertEquals(range.getLargestMinimum(), 1);
        assertEquals(range.getSmallestMaximum(), DAYS_PER_SEASON);
        assertEquals(range.getMaximum(), DAYS_PER_SEASON);
    }

    @Test
    public void test_monthOfYearRangeOnChronology() {
        ValueRange range = DiscordianChronology.INSTANCE.range(ChronoField.MONTH_OF_YEAR);
        assertEquals(range.getMinimum(), 0);
        assertEquals(range.getLargestMinimum(), 1);
        assertEquals(range.getSmallestMaximum(), 5);
        assertEquals(range.getMaximum(), 5);
    }

    //-----------------------------------------------------------------------
    // plus()
    //-----------------------------------------------------------------------

    @DataProvider(name = "plus")
    Object[][] data_plus() {
        return new Object[][] {
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), 1, ChronoUnit.DAYS, DiscordianChronology.INSTANCE.date(3179, 2, 30)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), 1, ChronoUnit.WEEKS, DiscordianChronology.INSTANCE.date(3179, 2, 34)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), 1, ChronoUnit.MONTHS, DiscordianChronology.INSTANCE.date(3179, 3, 29)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), 1, ChronoUnit.YEARS, DiscordianChronology.INSTANCE.date(3180, 2, 29)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), 1, ChronoUnit.CENTURIES, DiscordianChronology.INSTANCE.date(3279, 2, 29)},
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), 1, ChronoUnit.DAYS, DiscordianChronology.INSTANCE.date(3178, 1, 60)},
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), 1, ChronoUnit.WEEKS, DiscordianChronology.INSTANCE.date(3178, 1, 64)},
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), 1, ChronoUnit.MONTHS, DiscordianChronology.INSTANCE.date(3178, 2, 59)},
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), 1, ChronoUnit.YEARS, DiscordianChronology.INSTANCE.date(3179, 1, 59)},
        };
    }

    @Test(dataProvider = "plus")
    public void test_plus(DiscordianDate ddate, long value, TemporalUnit unit, DiscordianDate expected) {
        assertEquals(ddate.plus(value, unit), expected);
    }

    //-----------------------------------------------------------------------
    // periodUntil(Temporal, TemporalUnit)
    //-----------------------------------------------------------------------

    @DataProvider(name = "periods")
    Object[][] data_periods() {
        return new Object[][] {
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), DiscordianChronology.INSTANCE.date(3179, 2, 30), ChronoUnit.DAYS, 1},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), DiscordianChronology.INSTANCE.date(3179, 2, 28), ChronoUnit.DAYS, -1},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), DiscordianChronology.INSTANCE.date(3179, 3, 28), ChronoUnit.MONTHS, 0},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), DiscordianChronology.INSTANCE.date(3179, 3, 29), ChronoUnit.MONTHS, 1},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), DiscordianChronology.INSTANCE.date(3179, 4, 28), ChronoUnit.MONTHS, 1},
                {DiscordianChronology.INSTANCE.date(3179, 2, 29), DiscordianChronology.INSTANCE.date(3179, 2, 24), ChronoUnit.WEEKS, -1},
                {DiscordianChronology.INSTANCE.date(-1, 1, 1), DiscordianChronology.INSTANCE.date(3179, 2, 29), ChronoUnit.ERAS, 0},
        };
    }

    @Test(dataProvider = "periods")
    public void test_periodUntil(DiscordianDate ddate1, DiscordianDate ddate2, TemporalUnit unit, long expected) {
        assertEquals(ddate1.periodUntil(ddate2, unit), expected);
    }

    @DataProvider(name = "badPeriodUntilArguments")
    Object[][] data_badPeriodUntilArguments() {
        return new Object[][] {
                {DiscordianChronology.INSTANCE.dateNow(), ZonedDateTime.now(), ChronoUnit.DAYS},
                {DiscordianChronology.INSTANCE.dateNow(), CopticChronology.INSTANCE.dateNow(), ChronoUnit.DAYS},
        };
    }

    @Test(dataProvider = "badPeriodUntilArguments", expectedExceptions = DateTimeException.class)
    public void test_periodUntilBadArguments(Temporal start, Temporal end, TemporalUnit unit) {
        start.periodUntil(end, unit);
    }

    //-----------------------------------------------------------------------
    // periodUntil(ChronoLocalDate)
    //-----------------------------------------------------------------------

    @DataProvider(name = "periodDifferences")
    Object[][] data_periodDifferences() {
        return new Object[][] {
                {DiscordianChronology.INSTANCE.dateNow(), DiscordianChronology.INSTANCE.dateNow(), Period.ZERO},
                {DiscordianChronology.INSTANCE.date(3179, 1, 1), DiscordianChronology.INSTANCE.date(3179, 5, 73), Period.of(0, 4, 72)},
                {DiscordianChronology.INSTANCE.date(1, 1, 1), DiscordianChronology.INSTANCE.date(3179, 5, 73), Period.of(3178, 4, 72)},
                {DiscordianChronology.INSTANCE.date(3179, 5, 73), DiscordianChronology.INSTANCE.date(3179, 1, 1), Period.of(0, -4, -72)},
                {DiscordianChronology.INSTANCE.date(-1, 5, 73), DiscordianChronology.INSTANCE.date(1, 1, 1), Period.of(1, 0, 1)},
                // St. Tib's Day handling is a little odd as it is only counted in the period if it is one of the operands
                // this allows days > St. Tib's Day to always be exact years apart regardless of whether there was a leap
                // year in between.
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), DiscordianChronology.INSTANCE.date(3179, 1, 60), Period.of(1, 0, 0)},
                {DiscordianChronology.INSTANCE.date(3179, 1, 60), DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), Period.of(-1, 0, 0)},
                {DiscordianChronology.INSTANCE.date(3179, 1, 60), DiscordianChronology.INSTANCE.date(3178, 1, 60), Period.of(-1, 0, 0)},
                {DiscordianChronology.INSTANCE.date(3066, 1, 1), DiscordianChronology.INSTANCE.date(3179, 1, 1), Period.of(113, 0, 0)},
        };
    }

    @Test(dataProvider = "periodDifferences")
    public void test_periodDifference(ChronoLocalDate<?> startDate, ChronoLocalDate<?> endDate, Period expectedPeriod) {
        assertEquals(startDate.periodUntil(endDate), expectedPeriod);
    }

}
