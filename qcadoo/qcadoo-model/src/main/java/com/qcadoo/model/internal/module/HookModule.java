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

import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.api.InternalDataDefinitionService;
import com.qcadoo.plugin.api.Module;

public class HookModule extends Module {

    private final String pluginIdentifier;

    private final String modelName;

    private final String hookType;

    private final String hookClassName;

    private final String hookMethodName;

    private final InternalDataDefinitionService dataDefinitionService;

    private final Element hook;

    private final ModelXmlHolder modelXmlHolder;

    public HookModule(final String pluginIdentifier, final String modelName, final Element hook,
            final ModelXmlHolder modelXmlHolder, final InternalDataDefinitionService dataDefinitionService) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.modelName = modelName;
        this.hook = hook;
        this.modelXmlHolder = modelXmlHolder;
        this.hookType = hook.getName();
        this.hookClassName = hook.getAttributeValue("class");
        this.hookMethodName = hook.getAttributeValue("method");
        this.dataDefinitionService = dataDefinitionService;
    }

    @Override
    public void init() {
        modelXmlHolder.addHook(pluginIdentifier, modelName, hook);
    }

    @Override
    public void disableOnStartup() {
        disable();
    }

    @Override
    public void enable() {
        ((InternalDataDefinition) dataDefinitionService.get(pluginIdentifier, modelName)).getHook(hookType, hookClassName,
                hookMethodName).enable();
    }

    @Override
    public void disable() {
        ((InternalDataDefinition) dataDefinitionService.get(pluginIdentifier, modelName)).getHook(hookType, hookClassName,
                hookMethodName).disable();
    }

}
