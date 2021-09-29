/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.3
 * <p>
 * This file is part of Qcadoo.
 * <p>
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.internal.components.layout;

import com.qcadoo.view.internal.api.ComponentPattern;

import java.util.LinkedList;
import java.util.List;

public class FlowGridLayoutCell {

    private List<ComponentPattern> components;

    private int rowspan = 1;

    private int colspan = 1;

    private boolean available = true;

    private Integer minHeight;

    public List<ComponentPattern> getComponents() {
        return components;
    }

    public void addComponent(final ComponentPattern component) {
        if (components == null) {
            components = new LinkedList<ComponentPattern>();
        }
        components.add(component);
    }

    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(final int rowspan) {
        this.rowspan = rowspan;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(final int colspan) {
        this.colspan = colspan;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(final boolean available) {
        this.available = available;
    }

    public Integer getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Integer minHeight) {
        this.minHeight = minHeight;
    }
}