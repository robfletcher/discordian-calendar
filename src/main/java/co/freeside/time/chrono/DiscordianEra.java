package co.freeside.time.chrono;

import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.Locale;
import static java.time.temporal.ChronoField.ERA;

/**
 * The era in the Discordian calendar system.
 *
 * This class defines the single valid era for the Discordian chronology.
 *
 * @author Rob Fletcher
 */
public enum DiscordianEra implements Era {

  /**
   * The singleton instance for the Year of Our Lady of Discord era (1166-01-01
   * BCE - current).
   */
  YOLD;

  @Override
  public int getValue() {
    return ordinal();
  }

  public Chronology getChronology() {
    return DiscordianChronology.INSTANCE;
  }

  public ChronoLocalDate date(int yearOfEra, int month, int day) {
    return getChronology().date(this, yearOfEra, month, day);
  }

  public ChronoLocalDate dateYearDay(int yearOfEra, int dayOfYear) {
    return getChronology().dateYearDay(this, yearOfEra, dayOfYear);
  }

  @Override
  public String getDisplayName(TextStyle style, Locale locale) {
    return new DateTimeFormatterBuilder().appendText(ERA, style).toFormatter(locale).format(this);
  }

  @Override
  public boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField) {
      return field == ERA;
    }
    return field != null && field.isSupportedBy(this);
  }

  @Override
  public ValueRange range(TemporalField field) {
    if (field == ERA) {
      return field.range();
    } else if (field instanceof ChronoField) {
      throw new DateTimeException("Unsupported field: " + ((ChronoField) field).name());
    }
    return field.rangeRefinedBy(this);
  }

  @Override
  public int get(TemporalField field) {
    if (field == ERA) {
      return getValue();
    }
    return range(field).checkValidIntValue(getLong(field), field);
  }

  @Override
  public long getLong(TemporalField field) {
    if (field == ERA) {
      return getValue();
    } else if (field instanceof ChronoField) {
      throw new DateTimeException("Unsupported field: " + ((ChronoField) field).name());
    }
    return field.getFrom(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> R query(TemporalQuery<R> query) {
    if (query == TemporalQueries.chronology()) {
      return (R) getChronology();
    }
    return query.queryFrom(this);
  }

  @Override
  public Temporal adjustInto(Temporal temporal) {
    return temporal.with(ERA, getValue());
  }
}
