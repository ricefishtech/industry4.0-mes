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
package com.qcadoo.view.internal.module;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.qcadoo.plugin.api.ModuleFactory;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.hooks.AbstractViewHookDefinition;
import com.qcadoo.view.internal.hooks.HookFactory;
import com.qcadoo.view.internal.hooks.HookType;

public class ViewHookModuleFactory extends ModuleFactory<ViewHookModule> {

    @Autowired
    private HookFactory hookFactory;

    @Autowired
    private InternalViewDefinitionService viewDefinitionService;

    @Override
    protected ViewHookModule parseElement(final String pluginIdentifier, final Element element) {
        String plugin = getRequiredAttribute(element, "plugin");
        String view = getRequiredAttribute(element, "view");
        String hookTypeStr = getRequiredAttribute(element, "type");
        String className = getRequiredAttribute(element, "class");
        String method = getRequiredAttribute(element, "method");

        HookType hookType = HookType.parseString(hookTypeStr);
        AbstractViewHookDefinition hook = buildHook(pluginIdentifier, className, method, hookType);

        return new ViewHookModule(pluginIdentifier, viewDefinitionService, plugin, view, hook);
    }

    private AbstractViewHookDefinition buildHook(final String pluginIdentifier, final String className, final String method,
            final HookType hookType) {
        if (hookType == HookType.POST_CONSTRUCT) {
            return hookFactory.buildViewConstructionHook(className, method, pluginIdentifier);
        } else {
            return hookFactory.buildViewLifecycleHook(className, method, pluginIdentifier, hookType);
        }
    }

    @Override
    public String getIdentifier() {
        return "view-hook";
    }

}
