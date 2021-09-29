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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class GanttChartItemTooltipBuilder {

    private Optional<String> header = Optional.absent();

    private List<String> content = Lists.newArrayList();

    public GanttChartItemTooltip build() {
        return new GanttChartItemTooltip(header, content);
    }

    public GanttChartItemTooltipBuilder withHeader(final String header) {
        this.header = Optional.fromNullable(header);
        return this;
    }

    public GanttChartItemTooltipBuilder addLineToContent(final String contentLine) {
        Preconditions.checkArgument(contentLine != null);
        this.content.add(contentLine);
        return this;
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
        GanttChartItemTooltipBuilder rhs = (GanttChartItemTooltipBuilder) obj;
        return new EqualsBuilder().append(this.header, rhs.header).append(this.content, rhs.content).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(header).append(content).toHashCode();
    }
}
