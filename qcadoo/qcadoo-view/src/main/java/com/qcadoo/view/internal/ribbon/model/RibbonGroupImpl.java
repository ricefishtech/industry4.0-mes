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
package com.qcadoo.view.internal.ribbon.model;

import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class RibbonGroupImpl implements InternalRibbonGroup {

    private final String name;

    private String extensionPluginIdentifier;

    private final SecurityRole authorizationRole;

    private final List<InternalRibbonActionItem> items = new LinkedList<InternalRibbonActionItem>();

    public RibbonGroupImpl(final String name) {
        this(name, null);
    }

    public RibbonGroupImpl(final String name, final SecurityRole authorizationRole) {
        this.name = name;
        this.authorizationRole = authorizationRole;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SecurityRole getAuthorizationRole() {
        return authorizationRole;
    }

    @Override
    public List<RibbonActionItem> getItems() {
        return new LinkedList<RibbonActionItem>(items);
    }

    @Override
    public RibbonActionItem getItemByName(final String itemName) {
        for (RibbonActionItem item : items) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void addItem(final InternalRibbonActionItem item) {
        items.add(item);
    }

    @Override
    public JSONObject getAsJson() throws JSONException {
        JSONObject groupObject = new JSONObject();
        groupObject.put("name", name);
        JSONArray itemsArray = new JSONArray();
        for (InternalRibbonActionItem item : items) {
            itemsArray.put(item.getAsJson());
        }
        groupObject.put("items", itemsArray);
        return groupObject;
    }

    @Override
    public InternalRibbonGroup getCopy() {
        InternalRibbonGroup copy = new RibbonGroupImpl(name, authorizationRole);
        copy.setExtensionPluginIdentifier(extensionPluginIdentifier);
        for (InternalRibbonActionItem item : items) {
            copy.addItem(item.getCopy());
        }
        return copy;
    }

    @Override
    public InternalRibbonGroup getUpdate() {
        InternalRibbonGroup diff = new RibbonGroupImpl(name, authorizationRole);
        diff.setExtensionPluginIdentifier(extensionPluginIdentifier);
        boolean isDiffrence = false;
        for (InternalRibbonActionItem item : items) {
            if (item.isShouldBeUpdated()) {
                diff.addItem(item);
                isDiffrence = true;
            }
        }
        if (isDiffrence) {
            return diff;
        }
        return null;
    }

    @Override
    public String getExtensionPluginIdentifier() {
        return extensionPluginIdentifier;
    }

    @Override
    public void setExtensionPluginIdentifier(final String extensionPluginIdentifier) {
        this.extensionPluginIdentifier = extensionPluginIdentifier;
    }

    @Override
    public String toString() {
        return "RibbonGroupImpl [name=" + name + ", extensionPluginIdentifier=" + extensionPluginIdentifier + "]";
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(1, 31).append(getName()).append(getExtensionPluginIdentifier()).append(getItems().size())
                .toHashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RibbonGroupImpl other = (RibbonGroupImpl) obj;
        return new EqualsBuilder().append(getName(), other.getName())
                .append(getExtensionPluginIdentifier(), other.getExtensionPluginIdentifier())
                .append(getItems().size(), other.getItems().size()).isEquals();
    }

}
