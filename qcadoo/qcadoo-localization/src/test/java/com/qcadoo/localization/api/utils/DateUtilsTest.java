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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public final void shouldParseDateFromNullAsNull() {
        // given
        final Object input = null;

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertNull(result);
    }

    @Test
    public final void shouldParseDateFromEmptyStringAsNull() {
        // given
        final Object input = "";

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertNull(result);
    }

    @Test
    public final void shouldParseDateFromBlankStringAsNull() {
        // given
        final Object input = "  ";

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertNull(result);
    }

    @Test
    public final void shouldParseDateFromDateString() {
        // given
        final Object input = "2013-01-01";

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertDateEquals(new DateTime(2013, 1, 1, 0, 0, 0, 0), result);
    }

    @Test
    public final void shouldParseDateFromDateTimeString() {
        // given
        final Object input = "2013-01-01 12:30:45";

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertDateEquals(new DateTime(2013, 1, 1, 12, 30, 45, 0), result);
    }

    @Test
    public final void shouldParseDateFromLongTimestamp() {
        // given
        final DateTime dateTime = new DateTime(2013, 1, 1, 12, 30, 45, 0);
        final Object input = dateTime.getMillis();

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertDateEquals(dateTime, result);
    }

    @Test
    public final void shouldParseDateFromBigIntegerTimestamp() {
        // given
        final DateTime dateTime = new DateTime(2013, 1, 1, 12, 30, 45, 0);
        final Object input = BigInteger.valueOf(dateTime.getMillis());

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertDateEquals(dateTime, result);
    }

    @Test
    public final void shouldParseDateFromDate() {
        // given
        final DateTime dateTime = new DateTime(2013, 1, 1, 12, 30, 45, 0);
        final Object input = dateTime.toDate();

        // when
        final Date result = DateUtils.parseDate(input);

        // then
        assertDateEquals(dateTime, result);
    }

    @Test
    public final void shouldThrowExceptionIfInputTypeIsUnsupported() {
        // given
        final Object input = new Object();

        // when & then
        try {
            DateUtils.parseDate(input);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
            // success
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldThrowExceptionIfInputStringHasIncorrectFormat() {
        // given
        final Object input = "123 or some text";

        // when & then
        try {
            DateUtils.parseDate(input);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
            // success
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldFormatDate() {
        // given
        final Date date = new DateTime(2013, 1, 1, 12, 30, 45, 0).toDate();

        // when
        final String result = DateUtils.toDateString(date);

        // then
        assertEquals("2013-01-01", result);
    }

    @Test
    public final void shouldFormatDateReturnEmptyStringForNull() {
        // when
        final String result = DateUtils.toDateString(null);

        // then
        assertEquals("", result);
    }

    @Test
    public final void shouldFormatDateTime() {
        // given
        final Date date = new DateTime(2013, 1, 1, 12, 30, 45, 0).toDate();

        // when
        final String result = DateUtils.toDateTimeString(date);

        // then
        assertEquals("2013-01-01 12:30:45", result);
    }

    @Test
    public final void shouldFormatDateTimeReturnEmptyStringForNull() {
        // when
        final String result = DateUtils.toDateTimeString(null);

        // then
        assertEquals("", result);
    }

    @Test
    public final void shouldNotParseYearLessThan1500AndThrowParseException() throws ParseException {
        assertThrowParseException("1499");
    }

    @Test
    public final void shouldNotParseYearGreaterThan2500AndThrowParseException() throws ParseException {
        assertThrowParseException("2501");
    }

    @Test
    public final void shouldNotParseInvalidExpressionAndThrowParseException() throws ParseException {
        assertThrowParseException("wut?!");
        assertThrowParseException("200");
        assertThrowParseException("2000:3");
        assertThrowParseException("2003-02-31");
        assertThrowParseException("2000-01-01 12:62");
        assertThrowParseException("2013-13");
        assertThrowParseException("2013-01-25#11:37");
        assertThrowParseException("2012-01-20 11-23-59");
        assertThrowParseException("2012-01-20  11:23:59");
        assertThrowParseException("2012-01-20  11:23: 59");
        assertThrowParseException("2012-  01 -20  11:23 :59");
    }

    private void assertThrowParseException(final String expresion) {
        try {
            DateUtils.parseAndComplete(expresion, true);
            Assert.fail();
        } catch (ParseException pe) {
            // success
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldParseYearDown() throws ParseException {
        // given
        final String expr = "2013";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 1, 1, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearUp() throws ParseException {
        // given
        final String expr = "2013";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 12, 31, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearSeparatorDown() throws ParseException {
        // given
        final String expr = "2013-";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 1, 1, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearSeparatorUp() throws ParseException {
        // given
        final String expr = "2013-";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 12, 31, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDown() throws ParseException {
        // given
        final String expr = "2013-05";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 1, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthUp() throws ParseException {
        // given
        final String expr = "2013-05";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 31, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthSeparatorDown() throws ParseException {
        // given
        final String expr = "2013-05-";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 1, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthSeparatorUp() throws ParseException {
        // given
        final String expr = "2013-05-";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 31, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearOneDigitMonthDown() throws ParseException {
        // given
        final String expr = "2013-5";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 1, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearOneDigitMonthUp() throws ParseException {
        // given
        final String expr = "2013-5";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 31, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearOneDigitMonthSeparatorDown() throws ParseException {
        // given
        final String expr = "2013-5-";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 1, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearOneDigitMonthSeparatorUp() throws ParseException {
        // given
        final String expr = "2013-5-";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 31, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayDown() throws ParseException {
        // given
        final String expr = "2013-05-20";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayUp() throws ParseException {
        // given
        final String expr = "2013-05-20";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDaySeparatorDown() throws ParseException {
        // given
        final String expr = "2013-05-20 ";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDaySeparatorUp() throws ParseException {
        // given
        final String expr = "2013-05-20 ";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthOneDigitDayDown() throws ParseException {
        // given
        final String expr = "2013-05-2";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 2, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthOneDigitDayUp() throws ParseException {
        // given
        final String expr = "2013-05-2";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 2, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthOneDigitDaySeparatorDown() throws ParseException {
        // given
        final String expr = "2013-05-2 ";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 2, 0, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthOneDigitDaySeparatorUp() throws ParseException {
        // given
        final String expr = "2013-05-2 ";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 2, 23, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourSeparatorDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15:";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourSeparatorUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15:";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayOneDigitHourDown() throws ParseException {
        // given
        final String expr = "2013-05-20 9";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 9, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayOneDigitHourUp() throws ParseException {
        // given
        final String expr = "2013-05-20 9";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 9, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayOneDigitHourSeparatorDown() throws ParseException {
        // given
        final String expr = "2013-05-20 9:";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 9, 0, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayOneDigitHourSeparatorUp() throws ParseException {
        // given
        final String expr = "2013-05-20 9:";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 9, 59, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteSeparatorDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30:";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteSeparatorUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30:";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourOneDigitMinuteDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15:3";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 3, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourOneDigitMinuteUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15:3";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 3, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourOneDigitMinuteSeparatorDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15:3:";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 3, 0, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourOneDigitMinuteSeparatorUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15:3:";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 3, 59, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteSecondDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30:20";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 20, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteSecondUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30:20";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 20, 999), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteOneDigitSecondDown() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30:2";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 2, 0), date);
    }

    @Test
    public final void shouldParseYearMonthDayHourMinuteOneDigitSecondUp() throws ParseException {
        // given
        final String expr = "2013-05-20 15:30:2";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 20, 15, 30, 2, 999), date);
    }

    @Test
    public final void shouldParseShortestExpressionDown() throws ParseException {
        // given
        final String expr = "2013-5-2 9:3:2";
        final boolean upComplete = false;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 2, 9, 3, 2, 0), date);
    }

    @Test
    public final void shouldParseShortestExpressionUp() throws ParseException {
        // given
        final String expr = "2013-5-2 9:3:2";
        final boolean upComplete = true;

        // when
        final Date date = DateUtils.parseAndComplete(expr, upComplete);

        // then
        assertDateEquals(new DateTime(2013, 5, 2, 9, 3, 2, 999), date);
    }

    private void assertDateEquals(final DateTime expected, final Date actualDate) {
        final DateTime actual = new DateTime(actualDate);
        assertEquals(expected.getMillisOfSecond(), actual.getMillisOfSecond());
        assertEquals(expected.getSecondOfMinute(), actual.getSecondOfMinute());
        assertEquals(expected.getMinuteOfHour(), actual.getMinuteOfHour());
        assertEquals(expected.getHourOfDay(), actual.getHourOfDay());
        assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
        assertEquals(expected.getMonthOfYear(), actual.getMonthOfYear());
        assertEquals(expected.getYear(), actual.getYear());
    }

    @Test
    public final void shouldCopyReturnNullForNullArgument() {
        assertNull(DateUtils.copy(null));
    }

    @Test
    public final void shouldCopyReturnNewInstanceOfDate() {
        // given
        Date originalDate = new DateTime(2013, 1, 1, 0, 0, 0, 0).toDate();

        // when
        Date date = DateUtils.copy(originalDate);

        // then
        assertEquals(originalDate, date);
        originalDate.setTime(originalDate.getTime() - 10000);
        assertFalse(originalDate.equals(date));
    }
}
