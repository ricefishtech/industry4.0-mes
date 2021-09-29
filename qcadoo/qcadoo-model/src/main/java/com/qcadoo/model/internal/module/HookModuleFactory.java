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
package com.qcadoo.model.internal.module;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.qcadoo.model.internal.api.InternalDataDefinitionService;
import com.qcadoo.plugin.api.ModuleFactory;

public class HookModuleFactory extends ModuleFactory<HookModule> {

    @Autowired
    private ModelXmlHolder modelXmlHolder;

    @Autowired
    private InternalDataDefinitionService dataDefinitionService;

    @Override
    protected HookModule parseElement(final String pluginIdentifier, final Element element) {
        String targetPluginIdentifier = getRequiredAttribute(element, "plugin");
        String targetModelName = getRequiredAttribute(element, "model");

        Element hook = getOneElementContent(element);
        hook.setAttribute("sourcePluginIdentifier", pluginIdentifier);

        return new HookModule(targetPluginIdentifier, targetModelName, hook, modelXmlHolder, dataDefinitionService);
    }

    @Override
    public String getIdentifier() {
        return "model-hook";
    }

}
