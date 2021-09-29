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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MenuItemDefinitionTest {

    private static final String ITEM_PLUGIN = "itemPlugin";

    private static final String ITEM_NAME = "itemName";

    private static final String CATEGORY_NAME = "categoryName";

    private static final String ROLE_IDENTIFIER = "roleIdentifier";

    private static final String VIEW_PLUGIN = "viewPlugin";

    private static final String VIEW_NAME = "viewName";

    private static final String VIEW_URL = "viewUrl";

    private static final boolean ITEM_ACTIVE = true;

    @Test
    public final void shouldMenuItemDefinition() {
        // when
        MenuItemDefinition itemDefinition = MenuItemDefinition.create(ITEM_PLUGIN, ITEM_NAME, CATEGORY_NAME, ROLE_IDENTIFIER, ITEM_ACTIVE);

        // then
        assertEquals(ITEM_PLUGIN, itemDefinition.getPluginIdentifier());
        assertEquals(ITEM_NAME, itemDefinition.getName());
        assertEquals(CATEGORY_NAME, itemDefinition.getCategoryName());
        assertEquals(ROLE_IDENTIFIER, itemDefinition.getAuthRoleIdentifier());
        assertEquals(ITEM_ACTIVE, itemDefinition.isActive());

        assertNull(itemDefinition.getViewPluginIdentifier());
        assertNull(itemDefinition.getViewName());
        assertNull(itemDefinition.getUrl());
    }

    @Test
    public final void shouldCreateMenuViewItemDefinition() {
        // when
        MenuItemDefinition itemDefinition = MenuItemDefinition.create(ITEM_PLUGIN, ITEM_NAME, CATEGORY_NAME, ROLE_IDENTIFIER, ITEM_ACTIVE)
                .forView(VIEW_PLUGIN, VIEW_NAME);

        // then
        assertEquals(ITEM_PLUGIN, itemDefinition.getPluginIdentifier());
        assertEquals(ITEM_NAME, itemDefinition.getName());
        assertEquals(CATEGORY_NAME, itemDefinition.getCategoryName());
        assertEquals(ROLE_IDENTIFIER, itemDefinition.getAuthRoleIdentifier());

        assertEquals(VIEW_PLUGIN, itemDefinition.getViewPluginIdentifier());
        assertEquals(VIEW_NAME, itemDefinition.getViewName());
        assertEquals(ITEM_ACTIVE, itemDefinition.isActive());
        assertNull(itemDefinition.getUrl());
    }

    @Test
    public final void shouldMenuUrlItemDefinition() {
        // when
        MenuItemDefinition itemDefinition = MenuItemDefinition.create(ITEM_PLUGIN, ITEM_NAME, CATEGORY_NAME, ROLE_IDENTIFIER, ITEM_ACTIVE)
                .forUrl(VIEW_URL);

        // then
        assertEquals(ITEM_PLUGIN, itemDefinition.getPluginIdentifier());
        assertEquals(ITEM_NAME, itemDefinition.getName());
        assertEquals(CATEGORY_NAME, itemDefinition.getCategoryName());
        assertEquals(ROLE_IDENTIFIER, itemDefinition.getAuthRoleIdentifier());

        assertEquals(ITEM_PLUGIN, itemDefinition.getViewPluginIdentifier());
        assertEquals(ITEM_NAME, itemDefinition.getViewName());
        assertEquals(VIEW_URL, itemDefinition.getUrl());
        assertEquals(ITEM_ACTIVE, itemDefinition.isActive());
    }

}
