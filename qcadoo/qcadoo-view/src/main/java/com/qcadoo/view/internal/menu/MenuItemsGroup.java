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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents menu items group
 * 
 * @since 0.4.0
 * 
 */
public final class MenuItemsGroup {

    private final String name;

    private final String label;

    private final String description;

    private final List<MenuItem> items;

    /**
     * 
     * @param name
     *            identifier of group
     * @param label
     *            group label to display
     */
    public MenuItemsGroup(final String name, final String label, final String description) {
        super();
        this.name = name;
        this.label = label;
        this.description = description;
        items = new LinkedList<MenuItem>();
    }

    /**
     * Get identifier of group
     * 
     * @return identifier of group
     */
    public String getName() {
        return name;
    }

    /**
     * Get group label to display
     * 
     * @return group label to display
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get list of all items of group
     * 
     * @return list of all items of group
     */
    public List<MenuItem> getItems() {
        return items;
    }

    /**
     * Add item to menu group
     * 
     * @param item
     *            item to add
     */
    public void addItem(final MenuItem item) {
        items.add(item);
    }

    /**
     * Generates JSON representation of this menu group
     * 
     * @return JSON group representation
     * @throws JSONException
     */
    public JSONObject getAsJson() throws JSONException {
        JSONObject itemObject = new JSONObject();
        itemObject.put("name", name);
        itemObject.put("label", label);
        JSONArray itemsArray = new JSONArray();
        for (MenuItem item : items) {
            itemsArray.put(item.getAsJson());
        }
        itemObject.put("items", itemsArray);
        itemObject.put("description", description);
        return itemObject;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MenuItemsGroup that = (MenuItemsGroup) o;
        return new EqualsBuilder().append(label, that.label).append(name, that.name).append(description, that.description)
                .append(items, that.items).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(label).append(name).toHashCode();
    }
}
