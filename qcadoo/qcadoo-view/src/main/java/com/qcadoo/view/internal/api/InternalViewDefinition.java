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
package com.qcadoo.view.internal.api;

import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.internal.components.window.WindowComponentPattern;
import com.qcadoo.view.internal.hooks.AbstractViewHookDefinition;

public interface InternalViewDefinition extends ViewDefinition {

    String JSON_EVENT = "event";

    String JSON_EVENT_NAME = "name";

    String JSON_EVENT_COMPONENT = "component";

    String JSON_EVENT_ARGS = "args";

    String JSON_COMPONENTS = "components";

    String JSON_JS_FILE_PATHS = "jsFilePaths";

    Map<String, Object> prepareView(JSONObject jsonObject, Locale locale);

    ViewDefinitionState performEvent(JSONObject jsonObject, Locale locale) throws JSONException;

    boolean isMenuAccessible();

    void addJsFilePath(String jsFilePath);

    void registerComponent(String reference, String path, ComponentPattern pattern);

    void unregisterComponent(String reference, String path);

    String translateContextReferences(String context);

    void addHook(AbstractViewHookDefinition viewHookDefinition);

    void removeHook(AbstractViewHookDefinition viewHookDefinition);

    WindowComponentPattern getRootWindow();

    ComponentPattern getComponentByReference(String reference);

    void addComponentPattern(final ComponentPattern componentPattern);

    void initialize();

}
