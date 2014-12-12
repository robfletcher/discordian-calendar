package co.freeside.time.chrono

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.*
import java.time.chrono.Chronology
import java.time.chrono.HijrahChronology
import java.time.chrono.IsoChronology
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

import static co.freeside.time.chrono.DiscordianChronology.*
import static co.freeside.time.chrono.DiscordianDate.ST_TIBS_DAY

class DiscordianChronologySpec extends Specification {

  def "can find Discordian chronology by name"() {
    given:
    def chrono = Chronology.of("Discordian")

    expect:
    chrono != null
    chrono.id == "Discordian"
    chrono.getCalendarType() == null
    chrono.is(DiscordianChronology.INSTANCE)
  }

  @Shared data_samples = [
      // YOLD era starts in 1166 BC
      [DiscordianChronology.INSTANCE.date(0, 1, 1), LocalDate.of(-1166, 1, 1)],
      // sanity check current date
      [DiscordianChronology.INSTANCE.date(3179, 2, 19), LocalDate.of(2013, 4, 2)],
      // leap day occurs on day 60 of the year but is *not* considered part of the 1st season
      [DiscordianChronology.INSTANCE.date(3179, 1, 59), LocalDate.of(2013, 2, 28)],
      [DiscordianChronology.INSTANCE.date(3179, 1, 60), LocalDate.of(2013, 3, 1)],
      // non leap year
      [DiscordianChronology.INSTANCE.date(3178, 1, 60), LocalDate.of(2012, 3, 1)],
      // leap year
      [DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), LocalDate.of(2012, 2, 29)],
      [DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY + 1), LocalDate.of(2012, 3, 1)],
      // the season boundaries occur on the same Gregorian day every year...
      [DiscordianChronology.INSTANCE.date(3179, 1, 1), LocalDate.of(2013, 1, 1)],
      [DiscordianChronology.INSTANCE.date(3179, 1, 73), LocalDate.of(2013, 3, 14)],
      [DiscordianChronology.INSTANCE.date(3179, 2, 1), LocalDate.of(2013, 3, 15)],
      [DiscordianChronology.INSTANCE.date(3179, 2, 73), LocalDate.of(2013, 5, 26)],
      [DiscordianChronology.INSTANCE.date(3179, 3, 1), LocalDate.of(2013, 5, 27)],
      [DiscordianChronology.INSTANCE.date(3179, 3, 73), LocalDate.of(2013, 8, 7)],
      [DiscordianChronology.INSTANCE.date(3179, 4, 1), LocalDate.of(2013, 8, 8)],
      [DiscordianChronology.INSTANCE.date(3179, 4, 73), LocalDate.of(2013, 10, 19)],
      [DiscordianChronology.INSTANCE.date(3179, 5, 1), LocalDate.of(2013, 10, 20)],
      [DiscordianChronology.INSTANCE.date(3179, 5, 73), LocalDate.of(2013, 12, 31)],
      // ... even if it is a leap year
      [DiscordianChronology.INSTANCE.date(3178, 1, 1), LocalDate.of(2012, 1, 1)],
      [DiscordianChronology.INSTANCE.date(3178, 1, 73), LocalDate.of(2012, 3, 14)],
      [DiscordianChronology.INSTANCE.date(3178, 2, 1), LocalDate.of(2012, 3, 15)],
      [DiscordianChronology.INSTANCE.date(3178, 2, 73), LocalDate.of(2012, 5, 26)],
      [DiscordianChronology.INSTANCE.date(3178, 3, 1), LocalDate.of(2012, 5, 27)],
      [DiscordianChronology.INSTANCE.date(3178, 3, 73), LocalDate.of(2012, 8, 7)],
      [DiscordianChronology.INSTANCE.date(3178, 4, 1), LocalDate.of(2012, 8, 8)],
      [DiscordianChronology.INSTANCE.date(3178, 4, 73), LocalDate.of(2012, 10, 19)],
      [DiscordianChronology.INSTANCE.date(3178, 5, 1), LocalDate.of(2012, 10, 20)],
      [DiscordianChronology.INSTANCE.date(3178, 5, 73), LocalDate.of(2012, 12, 31)],
  ]

  @Unroll
  def "Discordian date #ddate converts to ISO #iso"() {
    expect:
    LocalDate.from(ddate) == iso

    where:
    data << data_samples
    ddate = data[0]
    iso = data[1]
  }

  @Unroll
  def "ISO date #iso converts to Discordian #ddate"() {
    DiscordianChronology.INSTANCE.date(iso) == ddate

    where:
    data << data_samples
    ddate = data[0]
    iso = data[1]
  }

  @Unroll
  def "#year, #season, #dayOfSeason is not a valid date"() {
    when:
    DiscordianChronology.INSTANCE.date(year, season, dayOfSeason)

    then:
    thrown DateTimeException

    where:
    year | season | dayOfSeason
    // out of range season
    3179 | 0 | 0
    3179 | -1 | 1
    3179 | 6 | 1
    // out of range day
    3179 | 1 | 0
    3179 | 1 | 74
  }

  @Unroll
  def "#base with #adjuster is #expected"() {
    expect:
    base.with(adjuster) == DiscordianChronology.INSTANCE.date(3179, 1, 73)

    where:
    base                                                         | adjuster                                       | expected
    DiscordianChronology.INSTANCE.date(3179, 1, 1)               | TemporalAdjusters.lastDayOfMonth()             | DiscordianChronology.INSTANCE.date(3179, 1, 73)
    DiscordianChronology.INSTANCE.date(3178, 1, ST_TIBS_DAY - 1) | TemporalAdjusters.lastDayOfMonth()             | DiscordianChronology.INSTANCE.date(3178, 1, 73)
    DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY) | TemporalAdjusters.lastDayOfMonth()             | DiscordianChronology.INSTANCE.date(3178, 1, 73)
    DiscordianChronology.INSTANCE.date(3179, 1, 1)               | TemporalAdjusters.lastInMonth(DayOfWeek.of(1)) | DiscordianChronology.INSTANCE.date(3179, 1, 71)
    DiscordianChronology.INSTANCE.date(3178, 1, 1)               | TemporalAdjusters.lastInMonth(DayOfWeek.of(1)) | DiscordianChronology.INSTANCE.date(3178, 1, 71)
  }

  @Unroll
  def "#jdate converts to a string as #expected"() {
    expect:
    jdate.toString() == expected

    where:
    jdate                                           | expected
    DiscordianChronology.INSTANCE.date(0, 1, 1)     | "Sweetmorn, Chaos 1, 0 YOLD"
    DiscordianChronology.INSTANCE.date(3179, 2, 19) | "Boomtime, Discord 19, 3179 YOLD"
    // 60th day of the year is St. Tib's Day if it is a leap year
    DiscordianChronology.INSTANCE.dateYearDay(3178, 60) | "St. Tib's Day! 3178 YOLD"
    DiscordianChronology.INSTANCE.dateYearDay(3179, 60) | "Setting Orange, Chaos 60, 3179 YOLD"
    // St. Tib's Day is not part of the week, so day names after it should be the same in a leap year
    DiscordianChronology.INSTANCE.date(3179, 1, 60) | "Setting Orange, Chaos 60, 3179 YOLD"
    DiscordianChronology.INSTANCE.date(3178, 1, 60) | "Setting Orange, Chaos 60, 3178 YOLD"
  }

  def "Discordian chronology equals itself"() {
    DiscordianChronology.INSTANCE == DiscordianChronology.INSTANCE
  }

  def "Discordian chronology does not equal another chronology"() {
    DiscordianChronology.INSTANCE != IsoChronology.INSTANCE
  }

  def "Discordian dates are comparable"() {
    expect:
    ddate1 < ddate2
    ddate2 > ddate1

    where:
    ddate1 = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY)
    ddate2 = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY + 1)
  }

  @Shared data_leapYears = [
      [DiscordianChronology.INSTANCE.date(3179, 1, 1), false],
      [DiscordianChronology.INSTANCE.date(3178, 1, 1), true],
      [DiscordianChronology.INSTANCE.date(3066, 1, 1), false],
      [DiscordianChronology.INSTANCE.date(3166, 1, 1), true],
  ]

  def test_isLeapYear() {
    ddate.isLeapYear() == isLeapYear
    DiscordianChronology.INSTANCE.isLeapYear(ddate.getYear()) == isLeapYear

    where:
    ddate                                          | isLeapYear
    DiscordianChronology.INSTANCE.date(3179, 1, 1) | false
    DiscordianChronology.INSTANCE.date(3178, 1, 1) | true
    DiscordianChronology.INSTANCE.date(3066, 1, 1) | false
    DiscordianChronology.INSTANCE.date(3166, 1, 1) | true
  }

  @Unroll
  def "#ddate has a #field minimum of #min and maximum of #max"() {
    expect:
    ddate.range(field).getMinimum() == min
    ddate.range(field).getMaximum() == max

    where:
    ddate                                          | field                     | min | max
    DiscordianChronology.INSTANCE.date(3179, 1, 1) | ChronoField.DAY_OF_WEEK   | 0   | DAYS_PER_WEEK
    DiscordianChronology.INSTANCE.date(3178, 1, 1) | ChronoField.DAY_OF_WEEK   | 1   | DAYS_PER_WEEK
    DiscordianChronology.INSTANCE.date(3066, 1, 1) | ChronoField.DAY_OF_WEEK   | 0   | DAYS_PER_WEEK
    DiscordianChronology.INSTANCE.date(3166, 1, 1) | ChronoField.DAY_OF_WEEK   | 1   | DAYS_PER_WEEK
    DiscordianChronology.INSTANCE.date(3179, 1, 1) | ChronoField.DAY_OF_MONTH  | 0   | DAYS_PER_SEASON
    DiscordianChronology.INSTANCE.date(3178, 1, 1) | ChronoField.DAY_OF_MONTH  | 1   | DAYS_PER_SEASON
    DiscordianChronology.INSTANCE.date(3066, 1, 1) | ChronoField.DAY_OF_MONTH  | 0   | DAYS_PER_SEASON
    DiscordianChronology.INSTANCE.date(3166, 1, 1) | ChronoField.DAY_OF_MONTH  | 1   | DAYS_PER_SEASON
    DiscordianChronology.INSTANCE.date(3179, 1, 1) | ChronoField.MONTH_OF_YEAR | 0   | SEASONS_PER_YEAR
    DiscordianChronology.INSTANCE.date(3178, 1, 1) | ChronoField.MONTH_OF_YEAR | 1   | SEASONS_PER_YEAR
    DiscordianChronology.INSTANCE.date(3066, 1, 1) | ChronoField.MONTH_OF_YEAR | 0   | SEASONS_PER_YEAR
    DiscordianChronology.INSTANCE.date(3166, 1, 1) | ChronoField.MONTH_OF_YEAR | 1   | SEASONS_PER_YEAR
  }

  @Unroll
  def "#field has a maximum of #max"() {
    expect:
    range.getMinimum() == 0
    range.getLargestMinimum() == 1
    range.getSmallestMaximum() == max
    range.getMaximum() == max

    where:
    field                     | max
    ChronoField.DAY_OF_WEEK   | DAYS_PER_WEEK
    ChronoField.DAY_OF_MONTH  | DAYS_PER_SEASON
    ChronoField.MONTH_OF_YEAR | 5

    range = DiscordianChronology.INSTANCE.range(field)
  }

  @Unroll("#ddate plus #value #unit is #expected")
  def "can add to Discordian dates"() {
    expect:
    ddate.plus(value, unit) == expected

    where:
    ddate                                                        | unit                 | expected
    DiscordianChronology.INSTANCE.date(3179, 2, 29)              | ChronoUnit.DAYS      | DiscordianChronology.INSTANCE.date(3179, 2, 30)
    DiscordianChronology.INSTANCE.date(3179, 2, 29)              | ChronoUnit.WEEKS     | DiscordianChronology.INSTANCE.date(3179, 2, 34)
    DiscordianChronology.INSTANCE.date(3179, 2, 29)              | ChronoUnit.MONTHS    | DiscordianChronology.INSTANCE.date(3179, 3, 29)
    DiscordianChronology.INSTANCE.date(3179, 2, 29)              | ChronoUnit.YEARS     | DiscordianChronology.INSTANCE.date(3180, 2, 29)
    DiscordianChronology.INSTANCE.date(3179, 2, 29)              | ChronoUnit.CENTURIES | DiscordianChronology.INSTANCE.date(3279, 2, 29)
    DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.DAYS      | DiscordianChronology.INSTANCE.date(3178, 1, 60)
    DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.WEEKS     | DiscordianChronology.INSTANCE.date(3178, 1, 64)
    DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.MONTHS    | DiscordianChronology.INSTANCE.date(3178, 2, 59)
    DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.YEARS     | DiscordianChronology.INSTANCE.date(3179, 1, 59)

    value = 1
  }

  @Unroll("#ddate1 and #ddate2 differ by #expected #unit")
  def "can calculate the difference between Discordian dates in different units"() {
    expect:
    ddate1.until(ddate2, unit) == expected

    where:
    ddate1                                          | ddate2                                          | unit              | expected
    DiscordianChronology.INSTANCE.date(3179, 2, 29) | DiscordianChronology.INSTANCE.date(3179, 2, 30) | ChronoUnit.DAYS   | 1
    DiscordianChronology.INSTANCE.date(3179, 2, 29) | DiscordianChronology.INSTANCE.date(3179, 2, 28) | ChronoUnit.DAYS   | -1
    DiscordianChronology.INSTANCE.date(3179, 2, 29) | DiscordianChronology.INSTANCE.date(3179, 3, 28) | ChronoUnit.MONTHS | 0
    DiscordianChronology.INSTANCE.date(3179, 2, 29) | DiscordianChronology.INSTANCE.date(3179, 3, 29) | ChronoUnit.MONTHS | 1
    DiscordianChronology.INSTANCE.date(3179, 2, 29) | DiscordianChronology.INSTANCE.date(3179, 4, 28) | ChronoUnit.MONTHS | 1
    DiscordianChronology.INSTANCE.date(3179, 2, 29) | DiscordianChronology.INSTANCE.date(3179, 2, 24) | ChronoUnit.WEEKS  | -1
    DiscordianChronology.INSTANCE.date(-1, 1, 1)    | DiscordianChronology.INSTANCE.date(3179, 2, 29) | ChronoUnit.ERAS   | 0
  }

  @Unroll("Attempting to get the difference between a Discordian date and a #end.class.simpleName causes an exception")
  def "cannot get the difference between a Discordian date and a date from a different chronology"() {
    when:
    start.until(end, unit);

    then:
    thrown DateTimeException

    where:
    end                                 | _
    ZonedDateTime.now()                 | _
    HijrahChronology.INSTANCE.dateNow() | _

    start = DiscordianChronology.INSTANCE.dateNow()
    unit = ChronoUnit.DAYS
  }

  @Unroll
  def "#startDate and #endDate differ by #expectedPeriod"() {
    expect:
    startDate.until(endDate) == expectedPeriod

    where:
    startDate                                       | endDate                                         | expectedPeriod
    DiscordianChronology.INSTANCE.dateNow()         | DiscordianChronology.INSTANCE.dateNow()         | Period.ZERO
    DiscordianChronology.INSTANCE.date(3179, 1, 1)  | DiscordianChronology.INSTANCE.date(3179, 5, 73) | Period.of(0, 4, 72)
    DiscordianChronology.INSTANCE.date(1, 1, 1)     | DiscordianChronology.INSTANCE.date(3179, 5, 73) | Period.of(3178, 4, 72)
    DiscordianChronology.INSTANCE.date(3179, 5, 73) | DiscordianChronology.INSTANCE.date(3179, 1, 1)  | Period.of(0, -4, -72)
    DiscordianChronology.INSTANCE.date(-1, 5, 73)   | DiscordianChronology.INSTANCE.date(1, 1, 1)     | Period.of(1, 0, 1)
    // St. Tib's Day handling is a little odd as it is only counted in the period if it is one of the operands
    // this allows days > St. Tib's Day to always be exact years apart regardless of whether there was a leap
    // year in between.
    DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY) | DiscordianChronology.INSTANCE.date(3179, 1, 60) | Period.of(1, 0, 0)
    DiscordianChronology.INSTANCE.date(3179, 1, 60) | DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY) | Period.of(-1, 0, 0)
    DiscordianChronology.INSTANCE.date(3179, 1, 60) | DiscordianChronology.INSTANCE.date(3178, 1, 60) | Period.of(-1, 0, 0)
    DiscordianChronology.INSTANCE.date(3066, 1, 1) | DiscordianChronology.INSTANCE.date(3179, 1, 1) | Period.of(113, 0, 0)
  }
}
