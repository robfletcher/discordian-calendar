package co.freeside.time.chrono;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;

/**
 * The Discordian calendar system.
 *
 * This chronology defines the rules of the Discordian calendar system.
 * The Discordian calendar system uses years of the same length as the ISO
 * calendar system. Each year has 5 _seasons_ of 73 days. The year is further
 * divided into 73 5-day weeks.
 *
 * Leap years occur on the same cycle as the ISO calendar system. The leap day
 * itself occurs on the same day of the year as in the ISO calendar. It is _not_
 * considered part of the week or season. i.e. _March 1st_ in the ISO calendar
 * is always _Chaos 60_ in the Discordian calendar regardless of whether it is a
 * leap year or not.
 *
 * @author Rob Fletcher
 */
public final class DiscordianChronology extends AbstractChronology
    implements Serializable {

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

  public DiscordianChronology() {
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

  @Override public ChronoLocalDate dateEpochDay(long epochDay) {
    return DiscordianDate.ofEpochDay(epochDay);
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
   * Translates a year in the Discordian calendar to a year in the ISO
   * calendar.
   */
  public int discordianToIsoYear(int discordianYear) {
    return discordianYear - ISO_YEAR_OFFSET;
  }

  /**
   * Translates a year in the ISO calendar to a year in the Discordian
   * calendar.
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
   * Translates a day of the week number in the range 1..5 to its standard
   * name.
   */
  public String getDayName(final int dayOfWeek) {
    checkValidDayOfWeek(dayOfWeek);
    return DAY_NAMES.get(dayOfWeek - 1);
  }

  /**
   * Asserts that `season` falls in the valid range. Throws
   * {@link DateTimeException} otherwise.
   */
  public void checkValidSeason(int season) {
    checkValueInRange(season, ChronoField.MONTH_OF_YEAR);
  }

  /**
   * Asserts that `dayOfSeason` falls in the valid range. Throws
   * {@link DateTimeException} otherwise.
   */
  public void checkValidDayOfSeason(int dayOfSeason) {
    checkValueInRange(dayOfSeason, ChronoField.DAY_OF_MONTH);
  }

  /**
   * Asserts that `dayOfWeek` falls in the valid range. Throws
   * {@link DateTimeException} otherwise.
   */
  public void checkValidDayOfWeek(int dayOfWeek) {
    checkValueInRange(dayOfWeek, ChronoField.DAY_OF_WEEK);
  }

  private void checkValueInRange(int value, ChronoField field) {
    final ValueRange range = range(field);
    if (!range.isValidIntValue(value)) {
      throw new DateTimeException(String.format("%d is not a valid Discordian %s. Valid values are %d..%d", value, field.name(), range.getMinimum(), range.getMaximum()));
    }
  }

}
