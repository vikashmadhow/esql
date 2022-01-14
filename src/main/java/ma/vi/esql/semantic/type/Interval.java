/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.esql.translation.TranslationException;

import static java.lang.Integer.parseInt;

/**
 * A value of interval type stored in the database.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Interval {
  public Interval(String value) {
    int years = 0;
    int months = 0;
    int weeks = 0;
    int days = 0;
    int hours = 0;
    int minutes = 0;
    int seconds = 0;
    int milliseconds = 0;

    for (String part: value.toUpperCase().split(",")) {
      part = part.trim();
      char indicator = part.charAt(part.length() - 1);
      int intervalPart = parseInt(part.substring(0, part.length() - 1));
      switch (indicator) {
        case 'Y':
          years = intervalPart;
          break;

        case 'N':
          months = parseInt(part.substring(0, part.length() - 3));
          break;

        case 'W':
          weeks = intervalPart;
          break;

        case 'D':
          days = intervalPart;
          break;

        case 'H':
          hours = intervalPart;
          break;

        case 'M':
          minutes = intervalPart;
          break;

        case 'S':
          if (part.endsWith("MS")) {
            milliseconds = parseInt(part.substring(0, part.length() - 2));
          } else {
            seconds = intervalPart;
          }
          break;

        default:
          throw new TranslationException("Suffix " + indicator + " is not recognised in intervals");
      }
    }

    this.years = years;
    this.months = months;
    this.weeks = weeks;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.milliseconds = milliseconds;
  }

  public Interval(int years, int months, int weeks, int days, int hours,
                  int minutes, int seconds, int milliseconds) {
    this.years = years;
    this.months = months;
    this.weeks = weeks;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.milliseconds = milliseconds;
  }

  public Interval add(Interval other) {
    return new Interval(
        years + other.years,
        months + other.months,
        weeks + other.weeks,
        days + other.days,
        hours + other.hours,
        minutes + other.minutes,
        seconds + other.seconds,
        milliseconds + other.milliseconds);
  }

  @Override
  public String toString() {
    StringBuilder st = new StringBuilder();
    st.append((years == 0         ? "" : years + "y"))
      .append((months == 0        ? "" : (st.length() == 0 ? "" : ",") + months       + "mon"))
      .append((weeks == 0         ? "" : (st.length() == 0 ? "" : ",") + weeks        + "w"))
      .append((days == 0          ? "" : (st.length() == 0 ? "" : ",") + days         + "d"))
      .append((hours == 0         ? "" : (st.length() == 0 ? "" : ",") + hours        + "h"))
      .append((minutes == 0       ? "" : (st.length() == 0 ? "" : ",") + minutes      + "m"))
      .append((seconds == 0       ? "" : (st.length() == 0 ? "" : ",") + seconds      + "s"))
      .append((milliseconds == 0  ? "" : (st.length() == 0 ? "" : ",") + milliseconds + "ms"));
    return st.toString();
  }

  public final int years;
  public final int months;
  public final int weeks;
  public final int days;
  public final int hours;
  public final int minutes;
  public final int seconds;
  public final int milliseconds;
}
