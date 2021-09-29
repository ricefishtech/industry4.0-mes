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
package com.qcadoo.view.api.components.ganttChart;

import java.util.Date;

/**
 * @since 0.4.3
 */
public interface GanttChartScale {

    Date getDateTo();

    void setDateTo(final Date dateTo);

    Date getDateFrom();

    void setDateFrom(final Date dateFrom);

    /**
     * Create a new chart item.
     *
     * @param rowName
     *            name of a gantt chart's row to which this item belongs
     * @param name
     *            label displayed on both item's box and its tooltip (visible on mouse hover)
     * @param entityId
     *            id of a corresponding entity
     * @param dateFrom
     *            start date & time
     * @param dateTo
     *            finish date & time
     * @return new chart item
     */
    GanttChartItem createGanttChartItem(final String rowName, final String name, final Long entityId, final Date dateFrom,
            final Date dateTo);

    /**
     * Create a new chart item
     * 
     * @param rowName
     *            name of a gantt chart's row to which this item belongs
     * @param label
     *            label displayed on this item's box
     * @param tooltip
     *            tooltip contents, visible on mouse hover
     * @param entityId
     *            id of a corresponding entity
     * @param dateFrom
     *            start date & time
     * @param dateTo
     *            finish date & time
     * @return new chart item
     * @since 1.4
     */
    GanttChartItem createGanttChartItem(final String rowName, final String label, final GanttChartItemTooltip tooltip, final Long entityId,
            final Date dateFrom, final Date dateTo);

    Boolean getIsDatesSet();

    void setIsDatesSet(final Boolean isDatesSet);

}
