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

import org.json.JSONException;
import org.json.JSONObject;

public class RibbonActionItemImpl implements InternalRibbonActionItem {

    private String name;

    private String accesskey;

    private Type type;

    private String icon;

    private String script;

    private String clickAction;

    private boolean enabled;

    private boolean defaultEnabled = true;

    private boolean permanentlyDisabled;

    private String message;

    private boolean shouldBeUpdated = false;

    @Override
    public final String getAction() {
        return clickAction;
    }

    @Override
    public final void setAction(final String clickAction) {
        this.clickAction = clickAction;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void setName(final String name) {
        this.name = name;
    }

    @Override
    public final String getAccesskey() {
        return accesskey;
    }

    @Override
    public final void setAccesskey(final String accesskey) {
        this.accesskey = accesskey;
    }

    @Override
    public final Type getType() {
        return type;
    }

    @Override
    public final void setType(final Type type) {
        this.type = type;
    }

    @Override
    public final String getIcon() {
        return icon;
    }

    @Override
    public final void setIcon(final String icon) {
        this.icon = icon;
    }

    @Override
    public JSONObject getAsJson() throws JSONException {
        JSONObject itemObject = new JSONObject();
        itemObject.put("name", name);
        if (accesskey != null) {
            itemObject.put("accesskey", accesskey);
        }
        itemObject.put("type", type);
        itemObject.put("icon", icon);
        itemObject.put("enabled", enabled);
        itemObject.put("permanentlyDisabled", permanentlyDisabled);
        itemObject.put("message", message);
        if (script != null) {
            itemObject.put("script", script);
        }
        itemObject.put("clickAction", clickAction);
        return itemObject;
    }

    @Override
    public String getScript() {
        return script;
    }

    @Override
    public void setScript(final String script) {
        this.script = script;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (permanentlyDisabled) {
            this.enabled = false;
        } else {
            this.enabled = enabled;
        }
    }

    @Override
    public void setDefaultEnabled(final boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    @Override
    public void setPermanentlyDisabled(final boolean permanentlyDisabled) {
        this.permanentlyDisabled = permanentlyDisabled;
        if (permanentlyDisabled) {
            setEnabled(false);
        } else {
            setEnabled(defaultEnabled);
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public InternalRibbonActionItem getCopy() {
        InternalRibbonActionItem copy = new RibbonActionItemImpl();
        copyFields(copy);
        return copy;
    }

    protected void copyFields(final InternalRibbonActionItem item) {
        item.setName(name);
        item.setAccesskey(accesskey);
        item.setType(type);
        item.setIcon(icon);
        item.setScript(script);
        item.setAction(clickAction);
        item.setEnabled(enabled);
        item.setDefaultEnabled(defaultEnabled);
        item.setMessage(message);
    }

    @Override
    public boolean isShouldBeUpdated() {
        return shouldBeUpdated;
    }

    @Override
    public void requestUpdate(final boolean shouldBeUpdated) {
        this.shouldBeUpdated = shouldBeUpdated;
    }
}
