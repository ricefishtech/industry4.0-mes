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
package com.qcadoo.view.internal.ribbon;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.patterns.AbstractComponentPattern;
import com.qcadoo.view.internal.ribbon.model.InternalRibbon;

public final class RibbonUtils {

    private static final String NAME = "name";

    private RibbonUtils() {

    }

    public static JSONObject translateRibbon(final InternalRibbon ribbon, final Locale locale,
            final AbstractComponentPattern pattern) throws JSONException {
        JSONObject json = ribbon.getAsJson(pattern.getApplicationContext().getBean(SecurityRolesService.class));

        for (int i = 0; i < json.getJSONArray("groups").length(); i++) {
            JSONObject group = json.getJSONArray("groups").getJSONObject(i);
            group.put(
                    "label",
                    pattern.getTranslationService().translate(pattern.getTranslationPath() + ".ribbon." + group.getString(NAME),
                            "qcadooView.ribbon." + group.getString(NAME), locale));
            translateRibbonItems(group, group.getString(NAME) + ".", locale, pattern);
        }

        return json;
    }

    private static void translateRibbonItems(final JSONObject owner, final String prefix, final Locale locale,
            final AbstractComponentPattern pattern) throws JSONException {
        if (owner.has("items")) {
            for (int j = 0; j < owner.getJSONArray("items").length(); j++) {
                JSONObject item = owner.getJSONArray("items").getJSONObject(j);

                String label = pattern.getTranslationService().translate(
                        pattern.getTranslationPath() + ".ribbon." + prefix + item.getString(NAME),
                        "qcadooView.ribbon." + prefix + item.getString(NAME), locale);
                item.put("label", label);

                if (item.has("script")) {
                    String script = item.getString("script");
                    if (script != null) {
                        item.put("script", pattern.prepareScript(script, locale));
                    }
                }

                if (item.has("message")) {
                    String message = item.getString("message");
                    if (message.contains(".")) {
                        message = pattern.getTranslationService().translate(message, locale);
                    } else {
                        message = pattern.getTranslationService().translate("qcadooView.message." + message, locale);
                    }
                    item.put("message", pattern.prepareScript(message, locale));
                }

                translateRibbonItems(item, prefix + item.getString(NAME) + ".", locale, pattern);
            }
        }
    }

    public static String translateRibbonAction(final String action, final ViewDefinition viewDefinition) {
        if (action == null) {
            return null;
        }
        if (viewDefinition == null) {
            return action;
        }

        Pattern p = Pattern.compile("#\\{([^\\}]+)\\}");
        Matcher m = p.matcher(action);

        String translateAction = action;

        while (m.find()) {
            ComponentPattern actionComponentPattern = ((InternalViewDefinition) viewDefinition).getComponentByReference(m
                    .group(1));

            if (actionComponentPattern == null) {
                throw new IllegalStateException("Cannot find component '" + m.group(1) + "' for action: " + action);
            }

            translateAction = translateAction.replace("#{" + m.group(1) + "}", "#{" + actionComponentPattern.getPath() + "}");
        }

        return translateAction;
    }
}
