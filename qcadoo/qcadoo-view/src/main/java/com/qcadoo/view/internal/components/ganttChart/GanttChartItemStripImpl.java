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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.components.ganttChart.GanttChartItemStrip;

public class GanttChartItemStripImpl implements GanttChartItemStrip {

    private final String color;

    private final int size;

    public GanttChartItemStripImpl(final String cssColor, final int size) {
        this.color = cssColor;
        this.size = size;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public JSONObject getAsJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("color", color);
        json.put("size", size);
        return json;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31).append(color).append(size).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final GanttChartItemStripImpl other = (GanttChartItemStripImpl) obj;
        return new EqualsBuilder().append(color, other.color).append(size, other.size).isEquals();
    }

}
