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
package com.qcadoo.view.internal.components.layout;

import java.util.LinkedList;
import java.util.List;

import com.qcadoo.view.internal.api.ComponentPattern;

public class SmallTabLayoutPatternTab {

    private String name;

    private List<ComponentPattern> components = new LinkedList<ComponentPattern>();

    public SmallTabLayoutPatternTab(final String name) {
        this.name = name;
    }

    public void addComponent(final ComponentPattern component) {
        components.add(component);
    }

    public String getName() {
        return name;
    }

    public List<ComponentPattern> getComponents() {
        return components;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setComponents(final List<ComponentPattern> components) {
        this.components = components;
    }
}
