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

import java.util.Date;

/**
 * This type represents date range with optional dates from and to.
 * 
 * @author Marcin Kubala
 * @since 1.2.1
 * @deprecated use org.joda.time.Interval
 */
@Deprecated
public final class DateRange {

    private final Long fromMillis;

    private final Long toMillis;

    /**
     * Build new instance of DateRange
     * 
     * @param from
     *            range's lower bound date. May be null.
     * @param to
     *            range's upper bound date. May be null.
     */
    public DateRange(final Date from, final Date to) {
        this.fromMillis = getMillis(from);
        this.toMillis = getMillis(to);
    }

    private Long getMillis(final Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    /**
     * Check if this date range contains given date.
     * 
     * @param date
     *            date to be checked
     * @return true if this date range contains given date
     */
    public boolean contains(final Date date) {
        Long dateMillis = getMillis(date);
        if (fromMillis == null) {
            return toMillis == null || dateMillis <= toMillis;
        }
        if (toMillis == null) {
            return dateMillis >= fromMillis;
        }
        return dateMillis >= fromMillis && dateMillis <= toMillis;
    }

    /**
     * Get lower bound date.
     * 
     * @return lower bound date or null
     */
    public Date getFrom() {
        return getDate(fromMillis);
    }

    /**
     * Get upper bound date.
     * 
     * @return upper bound date or null
     */
    public Date getTo() {
        return getDate(toMillis);
    }

    private Date getDate(final Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31).append(fromMillis).append(toMillis).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DateRange other = (DateRange) obj;
        return new EqualsBuilder().append(fromMillis, other.fromMillis).append(toMillis, other.toMillis).isEquals();
    }

}
