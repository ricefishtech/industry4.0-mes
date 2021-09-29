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
package com.qcadoo.view.internal.components.ganttChart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.context.i18n.LocaleContextHolder;

import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.view.api.components.ganttChart.GanttChartItem;
import com.qcadoo.view.api.components.ganttChart.GanttChartItemTooltip;

public class GanttChartItemFactory {

    private static final int PRECISION = 10;

    private final int interval;

    private final SimpleDateFormat format = new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, LocaleContextHolder.getLocale());

    public GanttChartItemFactory(final int interval) {
        this.interval = interval;
    }

    public GanttChartItem createGanttChartItem(final String rowName, final String name, final GanttChartItemTooltip tooltip,
            final Long entityId, final Date dateFrom, final Date dateTo, final Date itemDateFrom, final Date itemDateTo) {

        double from = getPosition(dateFrom, dateTo, itemDateFrom);
        double to = getPosition(dateFrom, dateTo, itemDateTo);

        return new GanttChartItemImpl(rowName, name, tooltip, entityId, format.format(itemDateFrom), format.format(itemDateTo),
                from, to);
    }

    private long getTimezoneOffset(final Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
    }

    private double getPosition(final Date dateFrom, final Date dateTo, final Date date) {

        long tmFrom = dateFrom.getTime();
        long tmItem = date.getTime();
        long tmTo = dateTo.getTime();

        long dateFromOffset = getTimezoneOffset(dateFrom);
        long dateOffset = getTimezoneOffset(date);
        long dateToOffset = getTimezoneOffset(dateTo);

        if (dateFromOffset != dateToOffset) {
            tmTo += (dateToOffset - dateFromOffset);
        }

        if (dateFromOffset != dateOffset) {
            tmItem += (dateOffset - dateFromOffset);
        }

        int tmInterval = 1000 * 60 * 60 * interval;

        if (tmItem <= tmFrom) {
            return 0;
        }

        if (tmItem >= tmTo + 86400000L) {
            return (double) (tmTo - tmFrom) / tmInterval;
        }

        int region = (int) (tmItem - tmFrom) / tmInterval;

        long tmRegion = tmFrom + (tmInterval * region);

        return ((int) ((region + ((double) (tmItem - tmRegion)) / tmInterval) * PRECISION)) / (double) PRECISION;
    }

}
