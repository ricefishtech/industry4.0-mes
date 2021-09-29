/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.localization.api.utils;

import static com.google.common.base.Optional.of;
import static com.qcadoo.commons.functional.Either.left;
import static com.qcadoo.commons.functional.Either.right;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.qcadoo.commons.functional.Either;

/**
 * Utility for date localization.
 * 
 * @since 0.4.0
 */
public final class DateUtils {

    private static final String PARSE_EXCEPTION_MSG = "Can't parse date from value '%s'";

    private static final String L_WRONG_DATE = "wrong date";

    /**
     * Date format.
     */
    public static final String L_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Date-time format.
     */
    public static final String L_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date-time format for report files.
     */
    public static final String L_REPORT_DATE_TIME_FORMAT = "yyyy_MM_dd_HH_mm_ss";

    private static final String[] SUPPORTED_PATTERNS = new String[] { L_DATE_TIME_FORMAT, "yyyy-MM-dd HH:mm:",
            "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:", "yyyy-MM-dd HH", "yyyy-MM-dd", "yyyy-MM-", "yyyy-MM", "yyyy-", "yyyy" };

    private DateUtils() {
    }

    /**
     * Parse string into date, with autocomplete missing month, day, hour, minute and second.
     * 
     * Examples with up-complete:
     * 
     * <ul>
     * <li>2010: 2010-12-31 23:59:59</li>
     * <li>2010-03: 2010-03-31 23:59:59</li>
     * <li>2010-03-06: 2010-03-06 23:59:59</li>
     * <li>2010-03-06 19: 2010-03-06 19:59:59</li>
     * <li>2010-03-06 19:30: 2010-03-06 19:30:59</li>
     * <li>2010-03-06 19:30:20: 2010-03-06 19:30:20</li>
     * </ul>
     * 
     * Examples with down-complete:
     * 
     * <ul>
     * <li>2010: 2010-01-01 00:00:00</li>
     * <li>2010-03: 2010-03-01 00:00:00</li>
     * <li>2010-03-06: 2010-03-06 00:00:00</li>
     * <li>2010-03-06 19: 2010-03-06 19:00:00</li>
     * <li>2010-03-06 19:30: 2010-03-06 19:30:00</li>
     * <li>2010-03-06 19:30:20: 2010-03-06 19:30:20</li>
     * </ul>
     * 
     * @param dateExpression
     *            string with date expression
     * @param upComplete
     *            true if up-complete, otherwise down-complete
     * @return parsed date
     * @throws ParseException
     *             if year, month, day, hour, minute or second is invalid or when year is &lt; 1500 or &gt; 2500
     */
    public static Date parseAndComplete(final String dateExpression, final boolean upComplete) throws ParseException {
        final String trimmedDateExpression = StringUtils.trim(dateExpression);
        DateTime parsedDate = new DateTime(org.apache.commons.lang3.time.DateUtils.parseDateStrictly(trimmedDateExpression,
                SUPPORTED_PATTERNS));

        final String[] dateAndTime = trimmedDateExpression.split(" ");
        if (dateAndTime.length > 2 || parsedDate.getYear() < 1500 || parsedDate.getYear() > 2500) {
            throw new ParseException(L_WRONG_DATE, 1);
        }

        return round(parsedDate, upComplete, dateAndTime).toDate();
    }

    private static DateTime round(final DateTime dateTime, final boolean upComplete, final String[] dateAndTime) {
        if (!upComplete) {
            return dateTime;
        }

        DateTime roundedDateTime = dateTime;
        if (dateAndTime.length > 0 && StringUtils.isNotBlank(dateAndTime[0])) {
            roundedDateTime = roundUpDate(roundedDateTime, dateAndTime[0]);
            if (dateAndTime.length > 1 && StringUtils.isNotBlank(dateAndTime[1])) {
                roundedDateTime = roundUpTime(roundedDateTime, dateAndTime[1]);
            } else {
                roundedDateTime = roundedDateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
            }
        }

        return roundedDateTime.withMillisOfSecond(999);
    }

    private static DateTime roundUpDate(final DateTime dateTime, final String dateExpressionPart) {
        DateTime roundedDate = dateTime;
        final String[] date = dateExpressionPart.split("-");
        if (date.length < 3 || StringUtils.isBlank(date[2])) {
            final int day = roundedDate.dayOfMonth().getMaximumValue();
            roundedDate = roundedDate.withDayOfMonth(day);
        }
        if (date.length < 2 || StringUtils.isBlank(date[1])) {
            roundedDate = roundedDate.withMonthOfYear(12);
        }
        return roundedDate;
    }

    private static DateTime roundUpTime(final DateTime dateTime, final String timeExpressionPart) {
        DateTime roundedDate = dateTime;
        final String[] time = timeExpressionPart.split(":");
        if (time.length < 1 || StringUtils.isBlank(time[0])) {
            roundedDate = roundedDate.withHourOfDay(23);
        }
        if (time.length < 2 || StringUtils.isBlank(time[1])) {
            roundedDate = roundedDate.withMinuteOfHour(59);
        }
        if (time.length < 3 || StringUtils.isBlank(time[2])) {
            roundedDate = roundedDate.withSecondOfMinute(59);
        }
        return roundedDate;
    }

    /**
     * Get date's String value in {@value DateUtils#L_DATE_TIME_FORMAT} format
     * 
     * @param date
     *            date to be formatted
     * @return date as String in {@value DateUtils#L_DATE_TIME_FORMAT} format or empty string if date is null
     */
    public static String toDateTimeString(final Date date) {
        return formatDate(date, DateUtils.L_DATE_TIME_FORMAT);
    }

    /**
     * Get date's String value in {@value DateUtils#L_DATE_FORMAT} format
     * 
     * @param date
     *            date to be formatted
     * @return date as String in {@value DateUtils#L_DATE_FORMAT} format or empty string if date is null
     */
    public static String toDateString(final Date date) {
        return formatDate(date, DateUtils.L_DATE_FORMAT);
    }

    private static String formatDate(final Date date, final String pattern) {
        if (date == null) {
            return "";
        }
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * Parse date from object
     * 
     * @param value
     *            object to be parsed. Supported argument types:
     *            <ul>
     *            <li>String - representing date in {@value DateUtils#L_DATE_TIME_FORMAT} or {@value DateUtils#L_DATE_FORMAT}
     *            format</li>
     *            <li>Date</li>
     *            <li>Number - containing number of milliseconds from 1st January 1970 00:00:00.000 GMT</li>
     *            </ul>
     * @return Date parsed from given object or null if object is null
     * @throws IllegalArgumentException
     *             if value is unsupported or has incorrect format
     */
    public static Date parseDate(final Object value) {
        Date date = null;
        if (value instanceof String) {
            if (StringUtils.isNotBlank((String) value)) {
                try {
                    date = org.apache.commons.lang3.time.DateUtils.parseDateStrictly((String) value, new String[] {
                            DateUtils.L_DATE_TIME_FORMAT, DateUtils.L_DATE_FORMAT });
                } catch (ParseException e) {
                    throw new IllegalArgumentException(String.format(PARSE_EXCEPTION_MSG, value), e);
                }
            }
        } else if (value instanceof Date) {
            // Date is mutable, make defensive copy to disallow implicit ('silent') modifications of the original one
            date = new Date(((Date) value).getTime());
        } else if (value instanceof Number) {
            date = new Date(((Number) value).longValue());
        } else if (value != null) {
            throw new IllegalArgumentException(String.format(PARSE_EXCEPTION_MSG, value));
        }
        return date;
    }

    public static Either<? extends Exception, Optional<DateTime>> tryParse(final Object value) {
        if (value instanceof String) {
            if (StringUtils.isNotBlank((String) value)) {
                try {
                    Date date = org.apache.commons.lang3.time.DateUtils.parseDateStrictly((String) value, new String[] {
                            DateUtils.L_DATE_TIME_FORMAT, DateUtils.L_DATE_FORMAT });
                    return right(of(new DateTime(date)));
                } catch (ParseException e) {
                    return left(new IllegalArgumentException(String.format(PARSE_EXCEPTION_MSG, value), e));
                }
            }
        } else if (value instanceof Date) {
            return right(of(new DateTime(value)));
        } else if (value instanceof Number) {
            return right(of(new DateTime(new Date(((Number) value).longValue()))));
        } else if (value != null) {
            return left(new IllegalArgumentException(String.format(PARSE_EXCEPTION_MSG, value)));
        }
        return right(Optional.<DateTime> absent());
    }

    public static Date copy(final Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

}
