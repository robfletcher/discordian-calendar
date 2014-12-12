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

  @Shared chronology = DiscordianChronology.INSTANCE

  def "can find Discordian chronology by name"() {
    given:
    def chrono = Chronology.of("Discordian")

    expect:
    chrono != null
    chrono.id == "Discordian"
    chrono.getCalendarType() == null
    chrono == chronology
  }

  @Shared data_samples = [
      // YOLD era starts in 1166 BC
      [chronology.date(0, 1, 1), LocalDate.of(-1166, 1, 1)],
      // sanity check current date
      [chronology.date(3179, 2, 19), LocalDate.of(2013, 4, 2)],
      // leap day occurs on day 60 of the year but is *not* considered part of the 1st season
      [chronology.date(3179, 1, 59), LocalDate.of(2013, 2, 28)],
      [chronology.date(3179, 1, 60), LocalDate.of(2013, 3, 1)],
      // non leap year
      [chronology.date(3178, 1, 60), LocalDate.of(2012, 3, 1)],
      // leap year
      [chronology.dateYearDay(3178, ST_TIBS_DAY), LocalDate.of(2012, 2, 29)],
      [chronology.dateYearDay(3178, ST_TIBS_DAY + 1), LocalDate.of(2012, 3, 1)],
      // the season boundaries occur on the same Gregorian day every year...
      [chronology.date(3179, 1, 1), LocalDate.of(2013, 1, 1)],
      [chronology.date(3179, 1, 73), LocalDate.of(2013, 3, 14)],
      [chronology.date(3179, 2, 1), LocalDate.of(2013, 3, 15)],
      [chronology.date(3179, 2, 73), LocalDate.of(2013, 5, 26)],
      [chronology.date(3179, 3, 1), LocalDate.of(2013, 5, 27)],
      [chronology.date(3179, 3, 73), LocalDate.of(2013, 8, 7)],
      [chronology.date(3179, 4, 1), LocalDate.of(2013, 8, 8)],
      [chronology.date(3179, 4, 73), LocalDate.of(2013, 10, 19)],
      [chronology.date(3179, 5, 1), LocalDate.of(2013, 10, 20)],
      [chronology.date(3179, 5, 73), LocalDate.of(2013, 12, 31)],
      // ... even if it is a leap year
      [chronology.date(3178, 1, 1), LocalDate.of(2012, 1, 1)],
      [chronology.date(3178, 1, 73), LocalDate.of(2012, 3, 14)],
      [chronology.date(3178, 2, 1), LocalDate.of(2012, 3, 15)],
      [chronology.date(3178, 2, 73), LocalDate.of(2012, 5, 26)],
      [chronology.date(3178, 3, 1), LocalDate.of(2012, 5, 27)],
      [chronology.date(3178, 3, 73), LocalDate.of(2012, 8, 7)],
      [chronology.date(3178, 4, 1), LocalDate.of(2012, 8, 8)],
      [chronology.date(3178, 4, 73), LocalDate.of(2012, 10, 19)],
      [chronology.date(3178, 5, 1), LocalDate.of(2012, 10, 20)],
      [chronology.date(3178, 5, 73), LocalDate.of(2012, 12, 31)],
  ]

  @Unroll
  def "Discordian date #ddate converts to ISO #iso"() {
    expect:
    IsoChronology.INSTANCE.date(ddate) == iso

    where:
    data << data_samples
    ddate = data[0]
    iso = data[1]
  }

  @Unroll
  def "ISO date #iso converts to Discordian #ddate"() {
    chronology.date(iso) == ddate

    where:
    data << data_samples
    ddate = data[0]
    iso = data[1]
  }

  @Unroll
  def "#year, #season, #dayOfSeason is not a valid date"() {
    when:
    chronology.date(year, season, dayOfSeason)

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

  @Unroll("The Discordian representation of epoch date #epochDay is #expected")
  def "can get a Discordian date from an epoch day"() {
    expect:
    chronology.dateEpochDay(epochDay) == expected

    where:
    epochDay | expected
    0        | DiscordianDate.of(3136, 1, 1)
    16415    | DiscordianDate.of(3180, 5, 53)
  }

  @Unroll
  def "#base with #adjuster is #expected"() {
    expect:
    base.with(adjuster) == expected

    where:
    base                                      | adjuster                                       | expected
    chronology.date(3179, 1, 1)               | TemporalAdjusters.lastDayOfMonth()             | chronology.date(3179, 1, 73)
    chronology.date(3178, 1, ST_TIBS_DAY - 1) | TemporalAdjusters.lastDayOfMonth()             | chronology.date(3178, 1, 73)
    chronology.dateYearDay(3178, ST_TIBS_DAY) | TemporalAdjusters.lastDayOfMonth()             | chronology.date(3178, 1, 73)
    chronology.date(3179, 1, 1)               | TemporalAdjusters.lastInMonth(DayOfWeek.of(1)) | chronology.date(3179, 1, 71)
    chronology.date(3178, 1, 1)               | TemporalAdjusters.lastInMonth(DayOfWeek.of(1)) | chronology.date(3178, 1, 71)
  }

  @Unroll
  def "#ddate converts to a string as #expected"() {
    expect:
    ddate.toString() == expected

    where:
    ddate                        | expected
    chronology.date(0, 1, 1)     | "Sweetmorn, Chaos 1, 0 YOLD"
    chronology.date(3179, 2, 19) | "Boomtime, Discord 19, 3179 YOLD"
    // 60th day of the year is St. Tib's Day if it is a leap year
    chronology.dateYearDay(3178, 60) | "St. Tib's Day! 3178 YOLD"
    chronology.dateYearDay(3179, 60) | "Setting Orange, Chaos 60, 3179 YOLD"
    // St. Tib's Day is not part of the week, so day names after it should be the same in a leap year
    chronology.date(3179, 1, 60) | "Setting Orange, Chaos 60, 3179 YOLD"
    chronology.date(3178, 1, 60) | "Setting Orange, Chaos 60, 3178 YOLD"
  }

  def "Discordian chronology equals itself"() {
    chronology == chronology
  }

  def "Discordian chronology does not equal another chronology"() {
    chronology != IsoChronology.INSTANCE
  }

  def "Discordian dates are comparable"() {
    expect:
    ddate1 < ddate2
    ddate2 > ddate1

    where:
    ddate1 = chronology.dateYearDay(3178, ST_TIBS_DAY)
    ddate2 = chronology.dateYearDay(3178, ST_TIBS_DAY + 1)
  }

  @Shared data_leapYears = [
      [chronology.date(3179, 1, 1), false],
      [chronology.date(3178, 1, 1), true],
      [chronology.date(3066, 1, 1), false],
      [chronology.date(3166, 1, 1), true],
  ]

  def "Discordian dates have leap years"() {
    ddate.isLeapYear() == isLeapYear
    chronology.isLeapYear(ddate.getYear()) == isLeapYear

    where:
    ddate                       | isLeapYear
    chronology.date(3179, 1, 1) | false
    chronology.date(3178, 1, 1) | true
    chronology.date(3066, 1, 1) | false
    chronology.date(3166, 1, 1) | true
  }

  @Unroll
  def "#ddate has a #field minimum of #min and maximum of #max"() {
    expect:
    ddate.range(field).getMinimum() == min
    ddate.range(field).getMaximum() == max

    where:
    ddate                       | field                     | min | max
    chronology.date(3179, 1, 1) | ChronoField.DAY_OF_WEEK   | 1   | DAYS_PER_WEEK
    chronology.date(3178, 1, 1) | ChronoField.DAY_OF_WEEK   | 0   | DAYS_PER_WEEK
    chronology.date(3066, 1, 1) | ChronoField.DAY_OF_WEEK   | 1   | DAYS_PER_WEEK
    chronology.date(3166, 1, 1) | ChronoField.DAY_OF_WEEK   | 0   | DAYS_PER_WEEK
    chronology.date(3179, 1, 1) | ChronoField.DAY_OF_MONTH  | 1   | DAYS_PER_SEASON
    chronology.date(3178, 1, 1) | ChronoField.DAY_OF_MONTH  | 0   | DAYS_PER_SEASON
    chronology.date(3066, 1, 1) | ChronoField.DAY_OF_MONTH  | 1   | DAYS_PER_SEASON
    chronology.date(3166, 1, 1) | ChronoField.DAY_OF_MONTH  | 0   | DAYS_PER_SEASON
    chronology.date(3179, 1, 1) | ChronoField.MONTH_OF_YEAR | 1   | SEASONS_PER_YEAR
    chronology.date(3178, 1, 1) | ChronoField.MONTH_OF_YEAR | 0   | SEASONS_PER_YEAR
    chronology.date(3066, 1, 1) | ChronoField.MONTH_OF_YEAR | 1   | SEASONS_PER_YEAR
    chronology.date(3166, 1, 1) | ChronoField.MONTH_OF_YEAR | 0   | SEASONS_PER_YEAR
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

    range = chronology.range(field)
  }

  @Unroll("#ddate plus #value #unit is #expected")
  def "can add to Discordian dates"() {
    expect:
    ddate.plus(value, unit) == expected

    where:
    ddate                                     | unit                 | expected
    chronology.date(3179, 2, 29)              | ChronoUnit.DAYS      | chronology.date(3179, 2, 30)
    chronology.date(3179, 2, 29)              | ChronoUnit.WEEKS     | chronology.date(3179, 2, 34)
    chronology.date(3179, 2, 29)              | ChronoUnit.MONTHS    | chronology.date(3179, 3, 29)
    chronology.date(3179, 2, 29)              | ChronoUnit.YEARS     | chronology.date(3180, 2, 29)
    chronology.date(3179, 2, 29)              | ChronoUnit.CENTURIES | chronology.date(3279, 2, 29)
    chronology.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.DAYS      | chronology.date(3178, 1, 60)
    chronology.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.WEEKS     | chronology.date(3178, 1, 64)
    chronology.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.MONTHS    | chronology.date(3178, 2, 59)
    chronology.dateYearDay(3178, ST_TIBS_DAY) | ChronoUnit.YEARS     | chronology.date(3179, 1, 59)

    value = 1
  }

  @Unroll("#ddate1 and #ddate2 differ by #expected #unit")
  def "can calculate the difference between Discordian dates in different units"() {
    expect:
    ddate1.until(ddate2, unit) == expected

    where:
    ddate1                       | ddate2                       | unit              | expected
    chronology.date(3179, 2, 29) | chronology.date(3179, 2, 30) | ChronoUnit.DAYS   | 1
    chronology.date(3179, 2, 29) | chronology.date(3179, 2, 28) | ChronoUnit.DAYS   | -1
    chronology.date(3179, 2, 29) | chronology.date(3179, 3, 28) | ChronoUnit.MONTHS | 0
    chronology.date(3179, 2, 29) | chronology.date(3179, 3, 29) | ChronoUnit.MONTHS | 1
    chronology.date(3179, 2, 29) | chronology.date(3179, 4, 28) | ChronoUnit.MONTHS | 1
    chronology.date(3179, 2, 29) | chronology.date(3179, 2, 24) | ChronoUnit.WEEKS  | -1
    chronology.date(-1, 1, 1)    | chronology.date(3179, 2, 29) | ChronoUnit.ERAS   | 0
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

    start = chronology.dateNow()
    unit = ChronoUnit.DAYS
  }

  @Unroll
  def "#startDate and #endDate differ by #expectedPeriod"() {
    expect:
    startDate.until(endDate) == expectedPeriod

    where:
    startDate                    | endDate                      | expectedPeriod
    chronology.dateNow()         | chronology.dateNow()         | Period.ZERO
    chronology.date(3179, 1, 1)  | chronology.date(3179, 5, 73) | Period.of(0, 4, 72)
    chronology.date(1, 1, 1)     | chronology.date(3179, 5, 73) | Period.of(3178, 4, 72)
    chronology.date(3179, 5, 73) | chronology.date(3179, 1, 1)  | Period.of(0, -4, -72)
    chronology.date(-1, 5, 73)   | chronology.date(1, 1, 1)     | Period.of(1, 0, 1)
    // St. Tib's Day handling is a little odd as it is only counted in the period if it is one of the operands
    // this allows days > St. Tib's Day to always be exact years apart regardless of whether there was a leap
    // year in between.
    chronology.dateYearDay(3178, ST_TIBS_DAY) | chronology.date(3179, 1, 60) | Period.of(1, 0, 0)
    chronology.date(3179, 1, 60) | chronology.dateYearDay(3178, ST_TIBS_DAY) | Period.of(-1, 0, 0)
    chronology.date(3179, 1, 60) | chronology.date(3178, 1, 60) | Period.of(-1, 0, 0)
    chronology.date(3066, 1, 1) | chronology.date(3179, 1, 1) | Period.of(113, 0, 0)
  }
}
