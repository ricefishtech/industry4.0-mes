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

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.internal.api.InternalFieldDefinition;
import com.qcadoo.plugin.api.Module;

public class FieldModule extends Module {

    private final String pluginIdentifier;

    private final String modelName;

    private final String fieldName;

    private final DataDefinitionService dataDefinitionService;

    private final ModelXmlHolder modelXmlHolder;

    private final Element field;

    public FieldModule(final String pluginIdentifier, final String modelName, final Element field,
            final ModelXmlHolder modelXmlHolder, final DataDefinitionService dataDefinitionService) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.modelName = modelName;
        this.field = field;
        this.modelXmlHolder = modelXmlHolder;
        this.fieldName = field.getAttributeValue("name");
        this.dataDefinitionService = dataDefinitionService;
    }

    @Override
    public void init() {
        field.setAttribute("required", "false");
        modelXmlHolder.addField(pluginIdentifier, modelName, field);
    }

    @Override
    public void disableOnStartup() {
        disable();
    }

    @Override
    public void enable() {
        ((InternalFieldDefinition) dataDefinitionService.get(pluginIdentifier, modelName).getField(fieldName)).enable();
    }

    @Override
    public void disable() {
        ((InternalFieldDefinition) dataDefinitionService.get(pluginIdentifier, modelName).getField(fieldName)).disable();

    }

}
