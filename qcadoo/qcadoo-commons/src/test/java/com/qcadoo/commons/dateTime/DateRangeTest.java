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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Sets;

public class DateRangeTest {

    private static final DateTime DATE_FROM_PROTOTYPE = new DateTime(2013, 1, 1, 0, 0, 0, 0);

    private static final DateTime DATE_TO_PROTOTYPE = new DateTime(2013, 5, 10, 0, 0, 0, 0);

    @Test
    public final void shouldGettersReturnDefensiveCopyOfDates() {
        // given
        Date originalFrom = DATE_FROM_PROTOTYPE.toDate();
        Date originalTo = DATE_TO_PROTOTYPE.toDate();

        DateRange dateRange = new DateRange(originalFrom, originalTo);

        Date returnedFrom = dateRange.getFrom();
        Date returnedTo = dateRange.getTo();

        // when
        originalFrom.setTime(1000);
        originalTo.setTime(2000);
        returnedFrom.setTime(3000);
        returnedTo.setTime(4000);

        // then
        assertEquals(DATE_FROM_PROTOTYPE.toDate(), dateRange.getFrom());
        assertEquals(DATE_TO_PROTOTYPE.toDate(), dateRange.getTo());

        assertFalse(originalFrom.equals(dateRange.getFrom()));
        assertFalse(originalTo.equals(dateRange.getTo()));

        assertFalse(returnedFrom.equals(dateRange.getFrom()));
        assertFalse(returnedTo.equals(dateRange.getTo()));

        assertFalse(originalFrom.equals(returnedFrom));
        assertFalse(originalTo.equals(returnedTo));
    }

    @Test
    public final void shouldHashCodeAndEqualsBeImplementedCorrectly() {
        // given
        DateRange firstDateRange = new DateRange(DATE_FROM_PROTOTYPE.toDate(), DATE_TO_PROTOTYPE.toDate());
        DateRange secondDateRange = new DateRange(DATE_FROM_PROTOTYPE.toDate(), DATE_TO_PROTOTYPE.toDate());
        DateRange thirdDateRange = new DateRange(DATE_FROM_PROTOTYPE.plusDays(1).toDate(), DATE_TO_PROTOTYPE.plusDays(3).toDate());

        // when
        Set<DateRange> dateRangesSet = Sets.newHashSet(firstDateRange, secondDateRange, thirdDateRange);

        // then
        assertTrue(dateRangesSet.contains(firstDateRange));
        assertTrue(dateRangesSet.contains(secondDateRange));
        assertTrue(dateRangesSet.contains(thirdDateRange));

        assertEquals(2, dateRangesSet.size());

        assertEquals(firstDateRange.hashCode(), secondDateRange.hashCode());
        assertFalse(firstDateRange.hashCode() == thirdDateRange.hashCode());
        assertFalse(secondDateRange.hashCode() == thirdDateRange.hashCode());

        assertEquals(firstDateRange.hashCode(), firstDateRange.hashCode());
        assertEquals(secondDateRange.hashCode(), secondDateRange.hashCode());
        assertEquals(thirdDateRange.hashCode(), thirdDateRange.hashCode());

        assertEquals(firstDateRange, firstDateRange);
        assertEquals(secondDateRange, secondDateRange);
        assertEquals(thirdDateRange, thirdDateRange);

        assertEquals(firstDateRange, secondDateRange);
        assertEquals(secondDateRange, firstDateRange);

        assertFalse(secondDateRange.equals(thirdDateRange));
        assertFalse(thirdDateRange.equals(secondDateRange));

        assertFalse(thirdDateRange.equals(firstDateRange));
        assertFalse(firstDateRange.equals(thirdDateRange));
    }
}
