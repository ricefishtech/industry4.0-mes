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
package com.qcadoo.commons.dateTime;

import junit.framework.Assert;

import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

public class TimeRangeTest {

    @Test
    public final void shouldCreateTimeRange() {
        // given
        LocalTime startTime = new LocalTime(10, 30, 0);
        LocalTime endTime = new LocalTime(14, 45, 30);

        // when
        TimeRange timeRange = new TimeRange(startTime, endTime);

        // then
        Assert.assertEquals(startTime, timeRange.getFrom());
        Assert.assertEquals(endTime, timeRange.getTo());

        Assert.assertFalse(timeRange.startsDayBefore());

        Assert.assertFalse(timeRange.contains(startTime.minusHours(2)));
        Assert.assertTrue(timeRange.contains(startTime));
        Assert.assertTrue(timeRange.contains(startTime.plusHours(2)));
        Assert.assertTrue(timeRange.contains(endTime));
        Assert.assertFalse(timeRange.contains(endTime.plusHours(2)));

        LocalDate today = LocalDate.now();
        Assert.assertEquals(new Interval(today.toDateTime(startTime), today.toDateTime(endTime)), timeRange.toInterval(today));
    }

    @Test
    public final void shouldCreateTimeRangeStartingDayBefore() {
        // given
        LocalTime startTime = new LocalTime(21, 0, 0);
        LocalTime endTime = new LocalTime(10, 0, 0);

        // when
        TimeRange timeRange = new TimeRange(startTime, endTime);

        // then
        Assert.assertEquals(startTime, timeRange.getFrom());
        Assert.assertEquals(endTime, timeRange.getTo());

        Assert.assertTrue(timeRange.startsDayBefore());

        Assert.assertFalse(timeRange.contains(startTime.minusHours(2)));
        Assert.assertTrue(timeRange.contains(startTime));
        Assert.assertTrue(timeRange.contains(startTime.plusHours(2)));
        Assert.assertTrue(timeRange.contains(endTime));
        Assert.assertFalse(timeRange.contains(endTime.plusHours(2)));

        LocalDate today = LocalDate.now();
        Assert.assertEquals(new Interval(today.toDateTime(startTime), today.toDateTime(endTime).plusDays(1)),
                timeRange.toInterval(today));
    }

}
