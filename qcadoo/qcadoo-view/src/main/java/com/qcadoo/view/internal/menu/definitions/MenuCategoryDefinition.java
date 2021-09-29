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
package com.qcadoo.view.internal.menu.definitions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MenuCategoryDefinition {

    private final String pluginIdentifier;

    private final String name;

    private final String authRole;

    public MenuCategoryDefinition(final String pluginIdentifier, final String name, final String authRole) {
        this.pluginIdentifier = pluginIdentifier;
        this.name = name;
        this.authRole = authRole;
    }

    public String getPluginIdentifier() {
        return pluginIdentifier;
    }

    public String getName() {
        return name;
    }

    public String getAuthRole() {
        return authRole;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MenuCategoryDefinition that = (MenuCategoryDefinition) o;
        return new EqualsBuilder().append(name, that.name).append(pluginIdentifier, that.pluginIdentifier)
                .append(authRole, that.authRole).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(pluginIdentifier).toHashCode();
    }
}
