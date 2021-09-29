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

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public final class GanttChartItemTooltip {

    private final Optional<String> header;

    private final List<String> content;

    GanttChartItemTooltip(final Optional<String> header, final List<String> content) {
        this.header = header;
        this.content = content;
    }

    public Optional<String> getHeader() {
        return header;
    }

    public List<String> getContent() {
        return content;
    }

    public JSONObject getAsJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("header", header.orNull());
        json.put("content", new JSONArray(content));
        return json;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        GanttChartItemTooltip rhs = (GanttChartItemTooltip) obj;
        return new EqualsBuilder().append(this.header, rhs.header).append(this.content, rhs.content).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(header).append(content).toHashCode();
    }
}
