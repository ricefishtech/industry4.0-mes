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
package com.qcadoo.view.internal.menu.module;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.qcadoo.plugin.api.ModuleFactory;
import com.qcadoo.view.internal.api.InternalMenuService;
import com.qcadoo.view.internal.menu.definitions.MenuItemDefinition;

public class MenuUrlModuleFactory extends ModuleFactory<MenuModule> {

    @Autowired
    private InternalMenuService menuService;

    @Override
    protected MenuModule parseElement(final String pluginIdentifier, final Element element) {
        String menuName = getRequiredAttribute(element, "name");
        String menuCategory = getRequiredAttribute(element, "category");
        String menuUrl = getRequiredAttribute(element, "url");
        String authRoleIdentifier = getAttribute(element, "defaultAuthorizationRole");
        String itemActiveAttribute = getAttribute(element, "active");
        boolean itemActive = itemActiveAttribute == null ? true : Boolean.parseBoolean(itemActiveAttribute);

        MenuItemDefinition menuItemDefinition = MenuItemDefinition.create(pluginIdentifier, menuName, menuCategory,
                authRoleIdentifier, itemActive).forUrl(menuUrl);

        return new MenuModule(getIdentifier(), menuService, menuItemDefinition);
    }

    @Override
    public String getIdentifier() {
        return "menu-item-url";
    }

}
