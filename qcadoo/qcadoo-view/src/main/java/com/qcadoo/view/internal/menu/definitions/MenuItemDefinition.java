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

public class MenuItemDefinition {

    private final String pluginIdentifier;

    private final String name;

    private final String categoryName;

    private final String authRoleIdentifier;

    private final String viewPluginIdentifier;

    private final String viewName;

    private final String url;

    private final boolean active;

    public static MenuItemDefinition create(final String pluginIdentifier, final String name, final String categoryName,
            final String authRoleIdentifier, boolean active) {
        return new MenuItemDefinition(pluginIdentifier, name, categoryName, authRoleIdentifier, null, null, null, active);
    }

    public MenuItemDefinition forUrl(final String url) {
        return new MenuItemDefinition(pluginIdentifier, name, categoryName, authRoleIdentifier, pluginIdentifier, name, url,
                active);
    }

    public MenuItemDefinition forView(final String viewPlugin, final String viewName) {
        return new MenuItemDefinition(pluginIdentifier, name, categoryName, authRoleIdentifier, viewPlugin, viewName, null,
                active);
    }

    private MenuItemDefinition(final String pluginIdentifier, final String name, final String categoryName,
            final String authRoleIdentifier, final String viewPluginIdentifier, final String viewName, final String url,
            final boolean active) {
        this.pluginIdentifier = pluginIdentifier;
        this.name = name;
        this.categoryName = categoryName;
        this.authRoleIdentifier = authRoleIdentifier;
        this.viewPluginIdentifier = viewPluginIdentifier;
        this.viewName = viewName;
        this.url = url;
        this.active = active;
    }

    public String getAuthRoleIdentifier() {
        return authRoleIdentifier;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getName() {
        return name;
    }

    public String getPluginIdentifier() {
        return pluginIdentifier;
    }

    public String getViewPluginIdentifier() {
        return viewPluginIdentifier;
    }

    public String getViewName() {
        return viewName;
    }

    public String getUrl() {
        return url;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        MenuItemDefinition rhs = (MenuItemDefinition) obj;
        return new EqualsBuilder().append(this.pluginIdentifier, rhs.pluginIdentifier).append(this.name, rhs.name)
                .append(this.categoryName, rhs.categoryName).append(this.authRoleIdentifier, rhs.authRoleIdentifier)
                .append(this.active, rhs.active).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(pluginIdentifier).append(name).append(categoryName).append(authRoleIdentifier)
                .append(active).toHashCode();
    }

}
