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
package com.qcadoo.view.internal.components;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.internal.states.AbstractComponentState;

public class FieldComponentState extends AbstractComponentState implements FieldComponent {

    public static final String JSON_REQUIRED = "required";

    private String value;

    private final boolean defaultRequired;

    private boolean required;

    private boolean persistent;

    public FieldComponentState(final FieldComponentPattern pattern) {
        super(pattern);
        defaultRequired = pattern.isRequired();
        persistent = pattern.isPersistent();
    }

    @Override
    protected void initializeContext(final JSONObject json) throws JSONException {
        super.initializeContext(json);
        if (json.has(JSON_COMPONENT_OPTIONS) && !json.isNull(JSON_COMPONENT_OPTIONS)) {
            JSONObject jsonOptions = json.getJSONObject(JSON_COMPONENT_OPTIONS);
            passValueFromJson(jsonOptions);
        }
    }

    private void passValueFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_VALUE) && !json.isNull(JSON_VALUE)) {
            value = json.getString(JSON_VALUE);
        }
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        if (json.has(JSON_VALUE) && !json.isNull(JSON_VALUE)) {
            value = json.getString(JSON_VALUE);
        }
        if (json.has(JSON_REQUIRED) && !json.isNull(JSON_REQUIRED)) {
            required = json.getBoolean(JSON_REQUIRED);
        }
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_VALUE, value);
        json.put(JSON_REQUIRED, isRequired());
        return json;
    }

    @Override
    public void setFieldValue(final Object value) {
        this.value = value == null ? null : value.toString();
        requestRender();
    }

    @Override
    public Object getFieldValue() {
        return value;
    }

    @Override
    public boolean isRequired() {
        return required || defaultRequired;
    }

    @Override
    public void setRequired(final boolean required) {
        this.required = required;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public void requestComponentUpdateState() {
        requestUpdateState();
    }

}
