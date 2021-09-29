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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.google.common.base.Preconditions;

/**
 * Immutable, thread-safe class representing time range. Time ranges with end time before start time are interpreted as ending in
 * the next day (from start do midnight (inclusive) and from midnight to end time).
 * 
 * @author Marcin Kubala
 * @since 1.2.1
 * 
 */
public class TimeRange implements Comparable<TimeRange> {

    private final LocalTime from;

    private final LocalTime to;

    /**
     * Get new Time range
     * 
     * If time to is earlier than time from, range will be considered as ending at next day.
     * 
     * @param from
     *            lower bound
     * @param to
     *            upper bound
     */
    public TimeRange(final LocalTime from, final LocalTime to) {
        Preconditions.checkArgument(from != null, "Missing lower bound for time range.");
        Preconditions.checkArgument(to != null, "Missing upper bound for time range.");
        this.from = from;
        this.to = to;
    }

    /**
     * @return true if from is greater than to.
     */
    public boolean startsDayBefore() {
        return to.isBefore(from);
    }

    /**
     * Check if given time is included in this time range. If from & to boundaries is in reversed order (time from is greater than
     * to; startsDayBefore() returns true) then it will be checked that given time is contained in range [from time = midnight
     * (inclusive)] or [midnight - to time].
     * 
     * @param time
     * @return true if this time range contains given time.
     */
    public boolean contains(final LocalTime time) {
        if (startsDayBefore()) {
            return !time.isAfter(to) || !time.isBefore(from);
        }
        return !time.isAfter(to) && !time.isBefore(from);
    }

    public LocalTime getFrom() {
        return from;
    }

    public LocalTime getTo() {
        return to;
    }

    public Interval toInterval(final LocalDate date) {
        DateTime start = date.toDateTime(getFrom());
        DateTime end = date.toDateTime(getTo());
        if (startsDayBefore()) {
            end = end.plusDays(1);
        }
        return new Interval(start, end);
    }

    @Override
    public int compareTo(final TimeRange other) {
        return getFrom().compareTo(other.getFrom());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31).append(from).append(to).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TimeRange other = (TimeRange) obj;
        return new EqualsBuilder().append(from, other.from).append(to, other.to).isEquals();
    }

}
