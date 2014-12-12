package co.freeside.time.chrono;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.temporal.*;

/**
 * A date in the http://en.wikipedia.org/wiki/Discordian_calendar[Discordian
 * Calendar] system.
 *
 * Since a Discordian year is the same length and uses the same leap-year
 * pattern as an ISO year this class is simply a wrapper around
 * {@link LocalDate}.
 *
 * Note that since _St. Tib's Day+ is considered "outside" the Discordian season
 * and week the methods {@link #getDayOfWeek()}, {@link #getDayOfSeason()} and
 * {@link #getSeason()} will return `0` for an instance of this class that
 * represents the leap day (the 60th day of a leap year).
 *
 * Hail Eris! All hail Discordia!
 *
 * @author Rob Fletcher
 */
public final class DiscordianDate implements ChronoLocalDate, Serializable {

  public static final int ST_TIBS_DAY = 60;

  private static final long serialVersionUID = 5856281505361396284L;

  /**
   * The underlying ISO local date.
   */
  private final LocalDate isoDate;

  /**
   * Obtains an instance of `DiscordianDate` from the year-of-era,
   * season-of-year and day-of-season.
   *
   * Note it is not possible to construct an instance representing the leap-day
   * using this method as the leap day falls "outside" of the Discordian season.
   * Instead use {@link #ofLeapDay(int)}.
   *
   * @param year        the year-of-era to represent.
   * @param season      the season-of-year to represent, from 1 to 5.
   * @param dayOfSeason the day-of-season to represent, from 1 to 73.
   * @return the Discordian date, never null.
   * @throws DateTimeException if the value of any field is out of range.
   */
  public static DiscordianDate of(int year, int season, int dayOfSeason) {
    DiscordianChronology chronology = DiscordianChronology.INSTANCE;
    chronology.checkValidSeason(season);
    chronology.checkValidDayOfSeason(dayOfSeason);
    int isoYear = chronology.discordianToIsoYear(year);
    int dayOfYear = dayOfDiscordianYear(year, season, dayOfSeason);
    return new DiscordianDate(LocalDate.ofYearDay(isoYear, dayOfYear));
  }

  /**
   * Obtains an instance of `DiscordianDate` from the year-of-era and
   * day-of-year.
   *
   * @param year      the year-of-era to represent.
   * @param dayOfYear the day-of-year to represent, from 1 to 365 (366 if `year`
   *                  is a leap year).
   * @return the Discordian date, never null.
   * @throws DateTimeException if the value of any field is out of range.
   */
  public static DiscordianDate of(int year, int dayOfYear) {
    int isoYear = DiscordianChronology.INSTANCE.discordianToIsoYear(year);
    return new DiscordianDate(LocalDate.ofYearDay(isoYear, dayOfYear));
  }

  /**
   * Obtains an instance of `DiscordianDate` representing the leap day in the
   * specified year.
   *
   * This method does _not_ validate input so if `year` is not a leap year in
   * the Discordian calendar the instance returned will represent Chaos 60.
   *
   * @param year the year-of-era to represent.
   * @return the Discordian date, never null.
   * @throws DateTimeException if the value of any field is out of range.
   */
  public static DiscordianDate ofLeapDay(int year) {
    return of(year, ST_TIBS_DAY);
  }

  public static DiscordianDate ofEpochDay(long epochDay) {
    return new DiscordianDate(LocalDate.ofEpochDay(epochDay));
  }

  private static int dayOfDiscordianYear(int prolepticYear, int season, int dayOfSeason) {
    int dayOfYear = ((season - 1) * DiscordianChronology.DAYS_PER_SEASON) + dayOfSeason;
    if (DiscordianChronology.INSTANCE.isLeapYear(prolepticYear) && dayOfYear >= ST_TIBS_DAY) {
      dayOfYear++;
    }
    return dayOfYear;
  }

  DiscordianDate(LocalDate isoDate) {
    this.isoDate = isoDate;
  }

  /**
   * Gets the year in the Discordian era.
   */
  public int getYear() {
    return DiscordianChronology.INSTANCE.isoToDiscordianYear(isoDate.getYear());
  }

  /**
   * Gets the season represented by this instance in the range 1 to 5. If this
   * instance represents a leap day this method will return _0_.
   */
  public int getSeason() {
    int dayOfYear = getLeapAdjustedDayOfYear();
    if (dayOfYear == 0) {
      return 0;
    }
    return ((dayOfYear - 1) / DiscordianChronology.DAYS_PER_SEASON) + 1;
  }

  /**
   * Gets the day-of-season represented by this instance in the range 1 to 5. If
   * this instance represents a leap day this method will return _0_.
   */
  public int getDayOfSeason() {
    int dayOfYear = getLeapAdjustedDayOfYear();
    if (dayOfYear == 0) {
      return 0;
    }
    return ((dayOfYear - 1) % DiscordianChronology.DAYS_PER_SEASON) + 1;
  }

  /**
   * Gets the day-of-week represented by this instance in the range 1 to 5. If
   * this instance represents a leap day this method will return _0_.
   */
  public int getDayOfWeek() {
    int dayOfYear = getLeapAdjustedDayOfYear();
    if (dayOfYear == 0) {
      return 0;
    }
    return ((dayOfYear - 1) % DiscordianChronology.DAYS_PER_WEEK) + 1;
  }

  public String getSeasonName() {
    return DiscordianChronology.INSTANCE.getSeasonName(getSeason());
  }

  public String getDayName() {
    return DiscordianChronology.INSTANCE.getDayName(getDayOfWeek());
  }

  private boolean isLeapDay() {
    return isoDate.isLeapYear() && isoDate.getDayOfYear() == ST_TIBS_DAY;
  }

  /**
   * @return the day of the year adjusted to remove the leap day if this is a
   * leap year. The value is thus convertible to season, day of week or season
   * regardless of whether this is a leap year.
   */
  private int getLeapAdjustedDayOfYear() {
    int dayOfYear = isoDate.getDayOfYear();
    if (isoDate.isLeapYear()) {
      if (dayOfYear == ST_TIBS_DAY) {
        return 0;
      } else if (dayOfYear > ST_TIBS_DAY) {
        return dayOfYear - 1;
      }
    }
    return dayOfYear;
  }

  @Override
  public ChronoLocalDate plus(long amountToAdd, TemporalUnit unit) {
    if (unit instanceof ChronoUnit) {
      ChronoUnit f = (ChronoUnit) unit;
      switch (f) {
        case WEEKS:
          return plus(amountToAdd * DiscordianChronology.DAYS_PER_WEEK, ChronoUnit.DAYS);
        case MONTHS:
          return plus(amountToAdd * DiscordianChronology.DAYS_PER_SEASON, ChronoUnit.DAYS);
        case ERAS:
          throw new DateTimeException("Unable to add era, Discordian calendar system only has one era");
        default:
          return new DiscordianDate(isoDate.plus(amountToAdd, unit));
      }
    }
    return unit.addTo(this, amountToAdd);
  }

  private long getEpochMonth() {
    int season = getSeason();
    // we need to cheat a bit for St. Tib's Day
    if (isLeapDay()) {
      season = 1;
    }
    return (getYear() * DiscordianChronology.SEASONS_PER_YEAR) + (season - 1);
  }

  @Override
  public Period until(ChronoLocalDate endDate) {
    DiscordianDate end = new DiscordianDate(LocalDate.from(endDate));
    long totalMonths = end.getEpochMonth() - this.getEpochMonth();
    int days = (end.isLeapDay() ? ST_TIBS_DAY : end.getDayOfSeason()) - (isLeapDay() ? ST_TIBS_DAY : getDayOfSeason());
    if (totalMonths > 0 && days < 0) {
      totalMonths--;
      ChronoLocalDate calcDate = this.plus(totalMonths, ChronoUnit.MONTHS);
      days = (int) (end.toEpochDay() - calcDate.toEpochDay());
    } else if (totalMonths < 0 && days > 0) {
      totalMonths++;
      days -= end.lengthOfMonth();
    }
    long years = totalMonths / DiscordianChronology.SEASONS_PER_YEAR;
    int months = (int) (totalMonths % DiscordianChronology.SEASONS_PER_YEAR);
    return Period.of((int) years, months, days);
  }

  @Override
  public long until(Temporal endDateTime, TemporalUnit unit) {
    if (!ChronoLocalDate.class.isAssignableFrom(endDateTime.getClass())) {
      throw new DateTimeException("Unable to calculate period between objects of two different types");
    }
    ChronoLocalDate end = (ChronoLocalDate) endDateTime;
    if (end.getChronology() != getChronology()) {
      throw new DateTimeException("Unable to calculate period between two different chronologies");
    }

    if (unit instanceof ChronoUnit) {
      switch ((ChronoUnit) unit) {
        case WEEKS:
          return isoDate.until(LocalDate.from(end), ChronoUnit.DAYS) / DiscordianChronology.DAYS_PER_WEEK;
        case MONTHS:
          return isoDate.until(LocalDate.from(end), ChronoUnit.DAYS) / DiscordianChronology.DAYS_PER_SEASON;
        case ERAS:
          return 0;
        default:
          return isoDate.until(LocalDate.from(end), unit);
      }
    }
    return unit.between(this, endDateTime);
  }

  @Override
  public Chronology getChronology() {
    return DiscordianChronology.INSTANCE;
  }

  @Override
  public int lengthOfMonth() {
    return DiscordianChronology.DAYS_PER_SEASON;
  }

  @Override
  public ChronoLocalDate with(TemporalField field, long newValue) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      if (getLong(f) == newValue) {
        return this;
      }
      switch (f) {
        case DAY_OF_MONTH:
          if (isLeapDay()) {
            return DiscordianDate.of(getYear(), 1, (int) newValue);
          } else {
            return DiscordianDate.of(getYear(), getSeason(), (int) newValue);
          }
      }
      // TODO: review other fields, such as WEEK_OF_YEAR
      return with(isoDate.with(field, newValue));
    }
    return field.adjustInto(this, newValue);
  }

  @Override
  public ValueRange range(TemporalField field) {
    if (field instanceof ChronoField) {
      switch ((ChronoField) field) {
        case MONTH_OF_YEAR:
          return ValueRange.of(isLeapYear() ? 0 : 1, DiscordianChronology.SEASONS_PER_YEAR);
        case DAY_OF_MONTH:
          return ValueRange.of(isLeapYear() ? 0 : 1, DiscordianChronology.DAYS_PER_SEASON);
        case DAY_OF_WEEK:
          return ValueRange.of(isLeapYear() ? 0 : 1, DiscordianChronology.DAYS_PER_WEEK);
        default:
          return getChronology().range((ChronoField) field);
      }
    }
    return field.rangeRefinedBy(this);
  }

  @Override
  public long getLong(TemporalField field) {
    if (field instanceof ChronoField) {
      switch ((ChronoField) field) {
        case YEAR_OF_ERA:
        case YEAR:
          return getYear();
        case ERA:
          return 1L;
        case MONTH_OF_YEAR:
          return getSeason();
        case DAY_OF_MONTH:
          return getDayOfSeason();
        case DAY_OF_WEEK:
          return getDayOfWeek();
      }
      // TODO: review other fields
      return isoDate.getLong(field);
    }
    return field.getFrom(this);
  }

  /**
   * Returns a String representation of this instance.
   *
   * The format used is compatible with the default format output by
   * http://linuxcommand.org/man_pages/ddate1.html[the UNIX _ddate_ command].
   *
   * Because of the leap year handling in the Discordian calendar string
   * representations of dates are not lexically sortable. The leap day has no
   * season, day-of-season or day-of-week so it does not make sense to attempt a
   * numeric representation of those values.
   */
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    if (isLeapDay()) {
      buffer.append("St. Tib's Day! ");
    } else {
      buffer.append(getDayName())
            .append(", ")
            .append(getSeasonName())
            .append(" ")
            .append(getDayOfSeason())
            .append(", ");
    }
    buffer.append(getYear()).append(" ").append(getEra());
    return buffer.toString();
  }

}
