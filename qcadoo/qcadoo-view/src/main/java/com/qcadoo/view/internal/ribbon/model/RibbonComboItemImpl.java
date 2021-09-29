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

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.ribbon.RibbonActionItem;

public class RibbonComboItemImpl extends RibbonActionItemImpl implements InternalRibbonComboItem {

    private final List<InternalRibbonActionItem> items = new LinkedList<InternalRibbonActionItem>();

    @Override
    public List<RibbonActionItem> getItems() {
        return new LinkedList<RibbonActionItem>(items);
    }

    @Override
    public void addItem(final InternalRibbonActionItem item) {
        items.add(item);
    }

    @Override
    public JSONObject getAsJson() throws JSONException {
        JSONObject itemObject = super.getAsJson();
        JSONArray itemsArray = new JSONArray();
        for (InternalRibbonActionItem item : items) {
            itemsArray.put(item.getAsJson());
        }
        itemObject.put("items", itemsArray);
        return itemObject;
    }

    @Override
    public InternalRibbonActionItem getCopy() {
        RibbonComboItemImpl copy = new RibbonComboItemImpl();
        copyFields(copy);
        for (InternalRibbonActionItem item : items) {
            copy.addItem(item.getCopy());
        }
        return copy;
    }
}
