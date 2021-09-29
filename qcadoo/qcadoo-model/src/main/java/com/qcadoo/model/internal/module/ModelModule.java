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

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.api.InternalDataDefinitionService;
import com.qcadoo.plugin.api.Module;

public class ModelModule extends Module {

    private final String pluginIdentifier;

    private final String modelName;

    private final InternalDataDefinitionService dataDefinitionService;

    private final ModelXmlHolder modelXmlHolder;

    private final String resource;

    public ModelModule(final String pluginIdentifier, final String modelName, final String resource,
            final ModelXmlHolder modelXmlHolder, final InternalDataDefinitionService dataDefinitionService) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.modelName = modelName;
        this.resource = resource;
        this.modelXmlHolder = modelXmlHolder;
        this.dataDefinitionService = dataDefinitionService;
    }

    @Override
    public void init() {
        try {
            modelXmlHolder.put(pluginIdentifier, modelName,
                    new ClassPathResource(pluginIdentifier + "/" + resource).getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void disableOnStartup() {
        disable();
    }

    @Override
    public void enable() {
        ((InternalDataDefinition) dataDefinitionService.get(pluginIdentifier, modelName)).enable();
    }

    @Override
    public void disable() {
        ((InternalDataDefinition) dataDefinitionService.get(pluginIdentifier, modelName)).disable();
    }

}
