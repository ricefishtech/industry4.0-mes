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

import com.qcadoo.view.internal.components.ganttChart.GanttChartItemStripImpl;

/**
 * Factory for gantt chart item's strips.
 * 
 * @author maku
 * @since 1.2.1
 */
public final class GanttChartItemStripFactory {

    private GanttChartItemStripFactory() {
    };

    /**
     * Creates item's strip with given color and size.
     * 
     * @param color
     *            css color string. For example: "red", "#45DFA9", "rgb(10, 55, 255)"
     * @param size
     *            percentage size of strip
     * @return new GanttChartItemStrip instance
     */
    public static GanttChartItemStrip create(final String color, final int size) {
        return new GanttChartItemStripImpl(color, size);
    }

}
