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
package com.qcadoo.view.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qcadoo.model.api.aop.Monitorable;
import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.security.SecurityViewDefinitionRoleResolver;

@Service
public class ViewDefinitionServiceImpl implements InternalViewDefinitionService, SecurityViewDefinitionRoleResolver {

    private final Map<String, InternalViewDefinition> viewDefinitions = new HashMap<String, InternalViewDefinition>();

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    @PreAuthorize("isAuthenticated() and hasPermission(#pluginIdentifier + '#' + #viewName, 'viewDefinition', 'isAuthorizedToSee')")
    public ViewDefinition get(final String pluginIdentifier, final String viewName) {
        if (PluginUtils.isEnabled(pluginIdentifier)) {
            return getWithoutSession(pluginIdentifier, viewName);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public InternalViewDefinition getWithoutSession(final String pluginIdentifier, final String viewName) {
        String key = pluginIdentifier + "." + viewName;
        if (viewDefinitions.containsKey(key)) {
            return viewDefinitions.get(key);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public List<ViewDefinition> list() {
        List<ViewDefinition> result = new ArrayList<ViewDefinition>();
        for (ViewDefinition viewDefinition : viewDefinitions.values()) {
            if (PluginUtils.isEnabled(viewDefinition.getPluginIdentifier())) {
                result.add(viewDefinition);
            }
        }
        return result;
    }

    @Override
    @Transactional
    @Monitorable
    public void save(final InternalViewDefinition viewDefinition) {
        viewDefinitions.put(viewDefinition.getPluginIdentifier() + "." + viewDefinition.getName(), viewDefinition);
    }

    @Override
    @Transactional
    @Monitorable
    public void delete(final InternalViewDefinition viewDefinition) {
        viewDefinitions.remove(viewDefinition.getPluginIdentifier() + "." + viewDefinition.getName());
    }

    @Override
    public SecurityRole getRoleForView(final String pluginIdentifier, final String viewName) {
        ViewDefinition view = getWithoutSession(pluginIdentifier, viewName);
        if (view == null) {
            return null;
        }
        return view.getAuthorizationRole();
    }

    @Override
    public boolean viewExists(final String pluginIdentifier, final String viewName) {
        return getWithoutSession(pluginIdentifier, viewName) != null;
    }

}
