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
package com.qcadoo.view.internal.states;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.ContainerState;
import com.qcadoo.view.internal.api.InternalComponentState;

public abstract class AbstractContainerState extends AbstractComponentState implements ContainerState {

    public AbstractContainerState() {
        super();
    }

    public AbstractContainerState(final ComponentPattern pattern) {
        super(pattern);
    }

    private final Map<String, InternalComponentState> children = new HashMap<String, InternalComponentState>();

    @Override
    public final void initialize(final JSONObject json, final Locale locale) throws JSONException {
        super.initialize(json, locale);

        JSONObject childerJson = null;
        if (json.has(JSON_CHILDREN)) {
            childerJson = json.getJSONObject(JSON_CHILDREN);
        }

        final boolean containerIsPermanentlyDisabled = containerIsPermanentlyDisabled(json);

        for (Map.Entry<String, InternalComponentState> child : children.entrySet()) {
            JSONObject childJson = null;
            if (childerJson == null) {
                childJson = new JSONObject();
            } else {
                childJson = childerJson.getJSONObject(child.getKey());
            }
            if (containerIsPermanentlyDisabled) {
                childJson.put(JSON_PERMANENTLY_DISABLED, true);
            }
            child.getValue().initialize(childJson, locale);
        }
    }

    private boolean containerIsPermanentlyDisabled(final JSONObject json) throws JSONException {
        if (json.has(JSON_PERMANENTLY_DISABLED) && !json.isNull(JSON_PERMANENTLY_DISABLED)) {
            return json.getBoolean(JSON_PERMANENTLY_DISABLED);
        }
        return false;
    }

    @Override
    public JSONObject render() throws JSONException {
        JSONObject json = super.render();

        JSONObject childerJson = new JSONObject();

        for (Map.Entry<String, InternalComponentState> child : children.entrySet()) {
            childerJson.put(child.getKey(), child.getValue().render());
        }

        json.put(JSON_CHILDREN, childerJson);

        return json;
    }

    @Override
    public boolean isHasError() {
        if (super.isHasError()) {
            return true;
        }
        for (ComponentState child : children.values()) {
            if (child.isHasError()) {
                return true;
            }
        }
        return false;
    }

    protected void requestUpdateStateRecursively() {
        requestUpdateState();
        for (InternalComponentState child : children.values()) {
            if (child instanceof FieldComponent) {
                ((FieldComponent) child).requestComponentUpdateState();
            } else if (child instanceof AbstractContainerState) {
                ((AbstractContainerState) child).requestUpdateStateRecursively();
            }
        }
    }

    @Override
    public final Map<String, InternalComponentState> getChildren() {
        return children;
    }

    @Override
    public final InternalComponentState getChild(final String name) {
        return children.get(name);
    }

    @Override
    public final void addChild(final InternalComponentState state) {
        children.put(state.getName(), state);
    }

    @Override
    public InternalComponentState findChild(final String name) {
        checkNotNull(name, "name should be given");
        InternalComponentState referencedComponent = getChildren().get(name);
        if (referencedComponent != null) {
            return referencedComponent;
        }

        for (InternalComponentState component : getChildren().values()) {
            if (component instanceof ContainerState) {
                ContainerState innerContainer = (ContainerState) component;
                referencedComponent = innerContainer.findChild(name);
                if (referencedComponent != null) {
                    return referencedComponent;
                }
            }
        }
        return null;
    }

}
