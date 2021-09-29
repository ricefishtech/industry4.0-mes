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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.components.ganttChart.GanttChartItem;

public class GanttChartConflictItem extends GanttChartItemImpl {

    private final List<GanttChartItem> items = new ArrayList<GanttChartItem>();

    public GanttChartConflictItem(final String row, final String dateFrom, final String dateTo, final double from, final double to) {
        super(row, null, null, dateFrom, dateTo, from, to);
    }

    public void addItem(final GanttChartItem item) {
        items.add(item);
    }

    @Override
    public JSONObject getAsJson() throws JSONException {
        JSONObject json = super.getAsJson();

        JSONArray itemsArray = new JSONArray();
        for (GanttChartItem item : items) {
            itemsArray.put(item.getAsJson());
        }
        json.put("items", itemsArray);

        return json;
    }
}
