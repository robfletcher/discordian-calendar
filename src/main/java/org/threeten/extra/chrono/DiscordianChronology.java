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
package org.threeten.extra.chrono;

import java.io.*;
import java.util.*;
import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.time.zone.*;
import java.time.temporal.*;

/**
 * The Discordian calendar system.
 * <p/>
 * This chronology defines the rules of the Discordian calendar system.
 * The Discordian calendar system uses years of the same length as the ISO calendar system. Each year has 5
 * <em>seasons</em> of 73 days. The year is further divided into 73 5-day weeks.
 * <p/>
 * Leap years occur on the same cycle as the ISO calendar system. The leap day itself occurs on the same day
 * of the year as in the ISO calendar. It is <em>not</em> considered part of the week or season. i.e. <em>March
 * 1st</em> in the ISO calendar is always <em>Chaos 60</em> in the Discordian calendar regardless of whether it
 * is a leap year or not.
 *
 * @author Rob Fletcher
 */
public final class DiscordianChronology extends AbstractChronology implements Serializable {

    /**
     * Singleton instance of the Discordian chronology.
     */
    public static final DiscordianChronology INSTANCE = new DiscordianChronology();

    public static final int SEASONS_PER_YEAR = 5;
    public static final int DAYS_PER_SEASON = 73;
    public static final int DAYS_PER_WEEK = 5;

    public static final ValueRange MONTH_OF_YEAR_RANGE = ValueRange.of(0, 1, SEASONS_PER_YEAR, SEASONS_PER_YEAR);
    public static final ValueRange DAY_OF_MONTH_RANGE = ValueRange.of(0, 1, DAYS_PER_SEASON, DAYS_PER_SEASON);
    public static final ValueRange DAY_OF_WEEK_RANGE = ValueRange.of(0, 1, DAYS_PER_WEEK, DAYS_PER_WEEK);

    public static final int ISO_YEAR_OFFSET = 1166;

    private static final List<String> SEASON_NAMES = Arrays.asList("Chaos", "Discord", "Confusion", "Bureaucracy", "The Aftermath");
    private static final List<String> DAY_NAMES = Arrays.asList("Sweetmorn", "Boomtime", "Pungenday", "Prickle-Prickle", "Setting Orange");

    private static final long serialVersionUID = 5856281505361396284L;

    private DiscordianChronology() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String getId() {
        return "Discordian";
    }

    @Override
    public String getCalendarType() {
        return null;
    }

    @Override
    public ChronoLocalDate date(int prolepticYear, int season, int dayOfSeason) {
        return DiscordianDate.of(prolepticYear, season, dayOfSeason);
    }

    @Override
    public ChronoLocalDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return super.date(era, yearOfEra, month, dayOfMonth);
    }

    @Override
    public ChronoLocalDate date(TemporalAccessor temporal) {
        if (temporal instanceof DiscordianDate) {
            return (DiscordianDate) temporal;
        }
        return new DiscordianDate(LocalDate.from(temporal));
    }

    @Override
    public ChronoLocalDate dateYearDay(int prolepticYear, int dayOfYear) {
        return DiscordianDate.of(prolepticYear, dayOfYear);
    }

    @Override
    public ChronoLocalDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return super.dateYearDay(era, yearOfEra, dayOfYear);
    }

    @Override
    public ChronoLocalDate dateNow() {
        return super.dateNow();
    }

    @Override
    public ChronoLocalDate dateNow(ZoneId zone) {
        return super.dateNow(zone);
    }

    @Override
    public ChronoLocalDate dateNow(Clock clock) {
        return super.dateNow(clock);
    }

    @Override
    public ChronoLocalDateTime<DiscordianDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<DiscordianDate>) super.localDateTime(temporal);
    }

    @Override
    public ChronoZonedDateTime<DiscordianDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<DiscordianDate>) super.zonedDateTime(temporal);
    }

    @Override
    public ChronoZonedDateTime<DiscordianDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<DiscordianDate>) super.zonedDateTime(instant, zone);
    }

    @Override
    public boolean isLeapYear(long prolepticYear) {
        return IsoChronology.INSTANCE.isLeapYear(discordianToIsoYear((int) prolepticYear));
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        return yearOfEra;
    }

    @Override
    public Era eraOf(int eraValue) {
        return DiscordianEra.YOLD;
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(DiscordianEra.values());
    }

    @Override
    public ValueRange range(ChronoField field) {
        switch (field) {
            case MONTH_OF_YEAR:
                return MONTH_OF_YEAR_RANGE;
            case DAY_OF_MONTH:
                return DAY_OF_MONTH_RANGE;
            case DAY_OF_WEEK:
                return DAY_OF_WEEK_RANGE;
        }
        return IsoChronology.INSTANCE.range(field);
    }

    /**
     * Translates a year in the Discordian calendar to a year in the ISO calendar.
     */
    public int discordianToIsoYear(int discordianYear) {
        return discordianYear - ISO_YEAR_OFFSET;
    }

    /**
     * Translates a year in the ISO calendar to a year in the Discordian calendar.
     */
    public int isoToDiscordianYear(int isoYear) {
        return isoYear + ISO_YEAR_OFFSET;
    }

    /**
     * Translates a season number in the range 1..5 to its standard name.
     */
    public String getSeasonName(final int season) {
        checkValidSeason(season);
        return SEASON_NAMES.get(season - 1);
    }

    /**
     * Translates a day of the week number in the range 1..5 to its standard name.
     */
    public String getDayName(final int dayOfWeek) {
        checkValidDayOfWeek(dayOfWeek);
        return DAY_NAMES.get(dayOfWeek - 1);
    }

    /**
     * Asserts that <code>season</code> falls in the valid range. Throws {@link DateTimeException} otherwise.
     */
    public void checkValidSeason(int season) {
        checkValueInRange(season, ChronoField.MONTH_OF_YEAR);
    }

    /**
     * Asserts that <code>dayOfSeason</code> falls in the valid range. Throws {@link DateTimeException} otherwise.
     */
    public void checkValidDayOfSeason(int dayOfSeason) {
        checkValueInRange(dayOfSeason, ChronoField.DAY_OF_MONTH);
    }

    /**
     * Asserts that <code>dayOfWeek</code> falls in the valid range. Throws {@link DateTimeException} otherwise.
     */
    public void checkValidDayOfWeek(int dayOfWeek) {
        checkValueInRange(dayOfWeek, ChronoField.DAY_OF_WEEK);
    }

    private void checkValueInRange(int value, ChronoField field) {
        final ValueRange range = range(field);
        if (!range.isValidIntValue(value)) {
            throw new DateTimeException(String.format("%d is not a valid Discordian %s. Valid values are %d..%d", value, field.getName(), range.getMinimum(), range.getMaximum()));
        }
    }

}
