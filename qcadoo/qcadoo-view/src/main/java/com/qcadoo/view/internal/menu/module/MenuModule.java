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

import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleException;
import com.qcadoo.view.internal.api.InternalMenuService;
import com.qcadoo.view.internal.menu.definitions.MenuItemDefinition;

public class MenuModule extends Module {

    private final InternalMenuService menuService;

    private final MenuItemDefinition menuItemDefinition;

    private final String factoryIdentifier;

    public MenuModule(final String factoryIdentifier, final InternalMenuService menuService,
            final MenuItemDefinition menuItemDefinition) {
        super();

        this.factoryIdentifier = factoryIdentifier;
        this.menuService = menuService;
        this.menuItemDefinition = menuItemDefinition;
    }

    @Override
    public void multiTenantEnable() {
        try {
            menuService.addView(menuItemDefinition);
            menuService.createItem(menuItemDefinition);
        } catch (Exception e) {
            throw new ModuleException(menuItemDefinition.getPluginIdentifier(), factoryIdentifier, e);
        }
    }

    @Override
    public void multiTenantDisable() {
        menuService.removeItem(menuItemDefinition);
        menuService.removeView(menuItemDefinition);
    }
}
