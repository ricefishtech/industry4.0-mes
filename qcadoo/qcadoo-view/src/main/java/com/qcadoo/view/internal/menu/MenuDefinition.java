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
package com.qcadoo.view.internal.menu;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents top menu in application
 * 
 * @since 0.4.0
 */
public final class MenuDefinition {

    private final List<MenuItemsGroup> items;

    private MenuItemsGroup homeCategory;

    private MenuItemsGroup administrationCategory;

    public MenuDefinition() {
        items = new LinkedList<MenuItemsGroup>();
    }

    /**
     * Get all menu groups
     * 
     * @return menu groups
     */
    public List<MenuItemsGroup> getItems() {
        return items;
    }

    /**
     * Add group to menu
     * 
     * @param item
     */
    public void addItem(final MenuItemsGroup item) {
        items.add(item);
    }

    /**
     * Generates JSON string that contains all menu definition
     * 
     * @return JSON menu definition
     */
    public String getAsJson() {
        try {
            JSONArray menuItems = new JSONArray();
            for (MenuItemsGroup item : items) {
                menuItems.put(item.getAsJson());
            }
            JSONObject menuStructure = new JSONObject();
            menuStructure.put("menuItems", menuItems);

            if (homeCategory != null) {
                menuStructure.put("homeCategory", homeCategory.getAsJson());
            }

            if (administrationCategory != null) {
                menuStructure.put("administrationCategory", administrationCategory.getAsJson());
            }

            return menuStructure.toString();
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Returns administration category of this menu
     * 
     * @return administration category of this menu
     */
    public MenuItemsGroup getAdministrationCategory() {
        return administrationCategory;
    }

    /**
     * Sets content of administration category
     * 
     * @param administrationCategory
     *            new content of administration category
     */
    public void setAdministrationCategory(final MenuItemsGroup administrationCategory) {
        this.administrationCategory = administrationCategory;
    }

    /**
     * Returns home category of this menu
     * 
     * @return home category of this menu
     */
    public MenuItemsGroup getHomeCategory() {
        return homeCategory;
    }

    /**
     * Sets content of home category
     * 
     * @param homeCategory
     *            new content of home category
     */
    public void setHomeCategory(final MenuItemsGroup homeCategory) {
        this.homeCategory = homeCategory;
    }
}
