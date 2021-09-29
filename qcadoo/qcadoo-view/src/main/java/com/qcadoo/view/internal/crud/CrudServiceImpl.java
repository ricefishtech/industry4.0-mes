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
package com.qcadoo.view.internal.crud;

import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Preconditions;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.crud.CrudService;
import com.qcadoo.view.internal.api.InternalComponentState;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.ViewDefinitionService;

@Service
public class CrudServiceImpl implements CrudService {

    @Autowired
    private ViewDefinitionService viewDefinitionService;

    @Override
    public ModelAndView prepareView(final String pluginIdentifier, final String viewName, final Map<String, String> arguments,
            final Locale locale) {

        InternalViewDefinition viewDefinition = (InternalViewDefinition) viewDefinitionService.get(pluginIdentifier, viewName);
        Preconditions.checkState(viewDefinition != null, String.format("Can't find view '%s/%s'", pluginIdentifier, viewName));

        ModelAndView modelAndView = new ModelAndView("crud/crudView");

        String context = viewDefinition.translateContextReferences(arguments.get("context"));

        JSONObject jsonContext = new JSONObject();

        if (StringUtils.hasText(context)) {
            try {
                jsonContext = new JSONObject(context);
                viewDefinition.setJsonContext(jsonContext);
            } catch (JSONException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        modelAndView.addObject("model", viewDefinition.prepareView(jsonContext, locale));
        modelAndView.addObject("viewName", viewName);
        modelAndView.addObject("pluginIdentifier", pluginIdentifier);
        modelAndView.addObject("context", context);

        boolean popup = false;
        if (arguments.containsKey("popup")) {
            popup = Boolean.parseBoolean(arguments.get("popup"));
        }
        modelAndView.addObject("popup", popup);

        modelAndView.addObject("locale", locale.getLanguage());

        return modelAndView;
    }

    @Override
    public ViewDefinitionState invokeEvent(final String pluginIdentifier, final String viewName, final JSONObject body,
            final Locale locale) {
        InternalViewDefinition viewDefinition = (InternalViewDefinition) viewDefinitionService.get(pluginIdentifier, viewName);

        try {
            return viewDefinition.performEvent(body, locale);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public JSONObject invokeEventAndRenderView(final String pluginIdentifier, final String viewName, final JSONObject body,
            final Locale locale) {
        ViewDefinitionState state = invokeEvent(pluginIdentifier, viewName, body, locale);
        return renderView(state);
    }

    @Override
    public JSONObject renderView(final ViewDefinitionState state) {
        try {
            return ((InternalComponentState) state).render();
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public JSONObject performEvent(final String pluginIdentifier, final String viewName, final JSONObject body,
            final Locale locale) {
        return invokeEventAndRenderView(pluginIdentifier, viewName, body, locale);
    }
}
